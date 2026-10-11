package com.neofoc.app.drivers.maglumi;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

/**
 * The Maglumi does not use the ASTM E1381 framing (no frame number, checksum nor CR LF).
 * Per its "Specification of LIS interface" (B.3, B.6) a message is:
 *
 *   <ENQ>  ACK
 *   <STX>  ACK
 *   H|...<CR>P|1<CR>O|...<CR>R|...<CR>L|1|N<CR>  ACK
 *   <ETX>  ACK
 *   <EOT>  ACK
 *
 * Each step is extracted here as its own frame so that it gets ACKed. Some instruments send
 * the whole [STX]...[ETX] in one go: that case is extracted as a single frame.
 * The STX / ETX / content frames are all FRAME_TYPE_CONTINUITY: the AstmReceiver concatenates
 * their data and parses the records on EOT.
 */
public class MaglumiFrame extends AstmFrame {

    // Set when the extracted frame is the records block (H...L) received without STX / ETX
    private boolean contentBlock = false;

    public MaglumiFrame(FocInstrument instrument) {
        super(instrument);
    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(false);
        contentBlock = false;
        if (buffer.length() == 0) {
            return false;
        }
        String str = buffer.toString();
        char first = str.charAt(0);

        boolean extracted = false;
        if (first == STX) {
            int eIdx = indexOfFrameEnd(str, 0);
            if (eIdx > 0 && !hasControlCharBetween(str, 1, eIdx)) {
                // Whole [STX]...[ETX] in the buffer. If a standard trailer [CS][CS][CR][LF] follows, consume it too
                int consumeTo = eIdx;
                int lfIdx = str.indexOf(LF, eIdx);
                if (lfIdx > 0 && lfIdx <= eIdx + 4) {
                    consumeTo = lfIdx;
                }
                setDataWithFrame(new StringBuffer(str.substring(0, eIdx + 1)));
                buffer.replace(0, consumeTo + 1, "");
            } else {
                // Lone STX: the Maglumi waits for an ACK before sending the records
                setDataWithFrame(new StringBuffer(String.valueOf(STX)));
                buffer.replace(0, 1, "");
            }
            extracted = true;
        } else if (first == ETX || first == ENQ || first == ACK || first == NACK || first == EOT) {
            setDataWithFrame(new StringBuffer(String.valueOf(first)));
            buffer.replace(0, 1, "");
            if (first == ENQ) {
                while (buffer.length() > 0 && buffer.charAt(0) == ENQ) {
                    buffer.replace(0, 1, "");
                }
            }
            extracted = true;
        } else {
            // Records block: complete when the L record is terminated by CR, or when ETX arrives
            int ctrlIdx = indexOfControlChar(str);
            int endIdx = indexOfLastRecordEnd(str);
            if (endIdx >= 0 && (ctrlIdx < 0 || endIdx < ctrlIdx)) {
                setDataWithFrame(new StringBuffer(str.substring(0, endIdx + 1)));
                buffer.replace(0, endIdx + 1, "");
                contentBlock = true;
                extracted = true;
            } else if (ctrlIdx >= 0 && str.charAt(ctrlIdx) == ETX && ctrlIdx > 0) {
                setDataWithFrame(new StringBuffer(str.substring(0, ctrlIdx)));
                buffer.replace(0, ctrlIdx, "");
                contentBlock = true;
                extracted = true;
            } else if (ctrlIdx > 0) {
                // Text that does not belong to a message (ex: checksum trailer), drop it
                Globals.logString("Maglumi dropping unexpected characters: " + str.substring(0, ctrlIdx));
                buffer.replace(0, ctrlIdx, "");
                return extractAnswerFromBuffer(buffer);
            }
        }

        if (extracted && buffer.length() > 0) {
            // Something else is already in the buffer: do not answer this one, the next is handled right after
            setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(true);
        }
        return extracted;
    }

    @Override
    public void extractDataFromFrame() throws Exception {
        StringBuffer dataWithFrame = getDataWithFrame();
        String str = dataWithFrame != null ? dataWithFrame.toString() : "";

        if (contentBlock) {
            setBareData(str);
        } else if (str.equals(String.valueOf(STX)) || str.equals(String.valueOf(ETX))) {
            setBareData("");
        } else if (str.length() > 1 && str.charAt(0) == STX) {
            int eIdx = indexOfFrameEnd(str, 0);
            int start = 1;
            // Tolerate a frame number if the instrument ever sends one
            if (str.length() > 2 && Character.isDigit(str.charAt(1))) {
                start = 2;
            }
            setBareData(eIdx > start ? str.substring(start, eIdx) : "");
        } else {
            super.extractDataFromFrame();
        }
    }

    private void setBareData(String data) {
        setData(new StringBuffer(data));
        setType(FRAME_TYPE_CONTINUITY);
        setSequence(SEQUENCE_IRRELEVANT);
    }

    // Index of the CR ending the L record, -1 if not received yet
    private int indexOfLastRecordEnd(String str) {
        int lineStart = 0;
        while (lineStart < str.length()) {
            int cr = str.indexOf(CR, lineStart);
            if (cr < 0) {
                return -1;
            }
            if (str.charAt(lineStart) == 'L' && lineStart + 1 < str.length() && str.charAt(lineStart + 1) == FIELD_SEPERATOR) {
                return cr;
            }
            lineStart = cr + 1;
        }
        return -1;
    }

    private int indexOfFrameEnd(String str, int fromIdx) {
        int etx = str.indexOf(ETX, fromIdx);
        int etb = str.indexOf(ETB, fromIdx);
        if (etx < 0) return etb;
        if (etb < 0) return etx;
        return Math.min(etx, etb);
    }

    private int indexOfControlChar(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (isControlChar(str.charAt(i))) {
                return i;
            }
        }
        return -1;
    }

    private boolean hasControlCharBetween(String str, int from, int to) {
        for (int i = from; i < to; i++) {
            char c = str.charAt(i);
            if (c == ENQ || c == ACK || c == NACK || c == EOT || c == STX) {
                return true;
            }
        }
        return false;
    }

    private boolean isControlChar(char c) {
        return c == STX || c == ETX || c == ETB || c == ENQ || c == ACK || c == NACK || c == EOT;
    }
}
