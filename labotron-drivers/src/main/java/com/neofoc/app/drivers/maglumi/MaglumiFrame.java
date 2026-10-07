package com.neofoc.app.drivers.maglumi;

import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

/**
 * The Maglumi sends its results as one bare frame, without frame number, checksum nor CR LF:
 *
 *   [STX]H|...[CR]P|1[CR]O|...[CR]R|...[CR]L|1|N[CR][ETX][EOT]
 *
 * The base AstmFrame only extracts STX...LF frames and, on EOT, drops the whole buffer,
 * so the results were lost. This frame accepts STX...ETX (or ETB) and delegates every
 * other case (ENQ, ACK, NACK, EOT, standard frames) to AstmFrame.
 */
public class MaglumiFrame extends AstmFrame {

    public MaglumiFrame(FocInstrument instrument) {
        super(instrument);
    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(false);
        String str = buffer.toString();

        int sIdx = str.indexOf(STX);
        if (sIdx >= 0 && !hasControlCharBefore(str, sIdx)) {
            int eIdx = indexOfFrameEnd(str, sIdx);
            if (eIdx > sIdx) {
                // If a standard trailer [CS][CS][CR][LF] follows, consume it too
                int consumeTo = eIdx;
                int lfIdx = str.indexOf(LF, eIdx);
                if (lfIdx > 0 && lfIdx <= eIdx + 4) {
                    consumeTo = lfIdx;
                }
                setDataWithFrame(new StringBuffer(str.substring(sIdx, eIdx + 1)));
                buffer.replace(0, consumeTo + 1, "");
                // e.g. [ETX][EOT] in the same read: do not ACK, the EOT will be handled next
                setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(buffer.length() > 0);
                return true;
            }
        }

        if (str.isEmpty()) {
            return false;
        }
        return super.extractAnswerFromBuffer(buffer);
    }

    @Override
    public void extractDataFromFrame() throws Exception {
        StringBuffer dataWithFrame = getDataWithFrame();
        String str = dataWithFrame != null ? dataWithFrame.toString() : "";
        int eIdx = str.startsWith(String.valueOf(STX)) ? indexOfFrameEnd(str, 0) : -1;
        if (eIdx > 0) {
            int start = 1;
            // Tolerate a frame number if the instrument ever sends one
            if (str.length() > 2 && Character.isDigit(str.charAt(1))) {
                start = 2;
            }
            setData(new StringBuffer(str.substring(start, eIdx)));
            setType(FRAME_TYPE_CONTINUITY);
            setSequence(SEQUENCE_IRRELEVANT);
        } else {
            super.extractDataFromFrame();
        }
    }

    private int indexOfFrameEnd(String str, int fromIdx) {
        int etx = str.indexOf(ETX, fromIdx);
        int etb = str.indexOf(ETB, fromIdx);
        if (etx < 0) return etb;
        if (etb < 0) return etx;
        return Math.min(etx, etb);
    }

    private boolean hasControlCharBefore(String str, int idx) {
        for (int i = 0; i < idx; i++) {
            char c = str.charAt(i);
            if (c == ENQ || c == ACK || c == NACK || c == EOT) {
                return true;
            }
        }
        return false;
    }
}
