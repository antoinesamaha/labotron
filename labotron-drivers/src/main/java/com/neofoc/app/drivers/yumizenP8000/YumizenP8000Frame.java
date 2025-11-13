package com.neofoc.app.drivers.yumizenP8000;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class YumizenP8000Frame extends AstmFrame implements YumizenP8000Const {

    private int type = 0;

    public YumizenP8000Frame(FocInstrument instrument, int type) {
        super(instrument);
        this.type = type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public char getType() {
        return (char) type;
    }

//	public static YumizenP8000Frame newMOReFrame(Instrument instrument) {
//		YumizenP8000Frame frame = new YumizenP8000Frame(instrument, FRAME_TYPE_MORE);
//		frame.append2Data(ANY_MOR);
//		return frame;
//	}

    @Override
    public void createDataWithFrame() throws Exception {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append(ASCII.STX);
//		StringBuffer bufferForChecksum = new StringBuffer();
//		bufferForChecksum.append(String.valueOf(getData()));
//		String checkSum = computeChecksum(bufferForChecksum);
//		buffer.append(bufferForChecksum);
//		buffer.append(ASCII.ETX);
//		buffer.append(checkSum);
//		buffer.append(ASCII.CR);
//		setDataWithFrame(buffer);
    }

    @Override
    public void extractDataFromFrame() throws Exception {
        // ORC|NW|L604163002|L604163002|L604163002|||||20160416090430||||||||||||<CR>
        StringBuffer dataWithFrame = getDataWithFrame();
        setData(dataWithFrame);
    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        logString("YumizenFrame.extractAnswerFromBuffer");
        String str = buffer.toString();
        boolean extractionDone = false;
        String end = String.valueOf(CR);

        int eIdx = str.indexOf(end);
        if (eIdx >= 0) {
            logString("endIndex found at:" + eIdx);
            for (int i = 0; i < Types.length && !extractionDone; i++) {
                String start = Types[i];
                if (i != TYPE_FS) {
                    start = start + "|";//This is because images can introduce MSH...
                }
                int sIdx = str.indexOf(start);
                if (sIdx >= 0 && sIdx < eIdx) {
                    logString("found start index: " + sIdx);
                    setType(i);
                    StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx));
                    setDataWithFrame(response);
                    buffer.replace(0, eIdx + 1, "");
                    logString("after buffer replace:" + buffer.toString());
                    extractionDone = true;
                }
            }

            if (!extractionDone) {
                logString("Extraction not found end found start not found. Removed part from 0 till " + eIdx);
                //In this case there is no start for this END we need to truncate the part
                buffer.replace(0, eIdx + 1, "");
            }

        } else {
            logString("endIndex NOT found");
        }

        setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(false);

        //If we read the buffer and there is still characters in the buffer this means that we
        //do not need to answer that buffer because we already received a following message
        //and we only need to answer the following one.
        if (extractionDone && buffer.length() > 0) {
            Globals.logString("Message has been read Remaining In Buffer : " + buffer.toString());
            setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(true);
        } else {
            Globals.logString("Message has been read nothing In Buffer");
        }

        return extractionDone;
    }
}