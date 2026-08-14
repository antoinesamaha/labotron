package com.neofoc.app.drivers.vitekbci;

import com.foc.util.ASCII;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class VitekBCIFrame extends AstmFrame {

    public final static char FRAME_TYPE_RESULT_VITEK = 'm';

    public VitekBCIFrame(FocInstrument instrument) {
        this(instrument, AstmFrame.FRAME_TYPE_NONE);
    }

    public VitekBCIFrame(FocInstrument instrument, char type) {
        super(instrument, SEQUENCE_IRRELEVANT, type);
    }

//	public static YumizenP8000Frame newMOReFrame(Instrument instrument) {
//		YumizenP8000Frame frame = new YumizenP8000Frame(instrument, FRAME_TYPE_MORE);
//		frame.append2Data(ANY_MOR);
//		return frame;
//	}

    @Override
    public void createDataWithFrame() throws Exception {
        StringBuffer buffer = new StringBuffer();
        if (getType() == FRAME_TYPE_ENQ) {
            buffer.append(ENQ);
        } else if (getType() == FRAME_TYPE_ACK) {
            buffer.append(ACK);
        } else if (getType() == FRAME_TYPE_NACK) {
            buffer.append(NACK);
        } else if (getType() == FRAME_TYPE_LAST) {
            buffer.append(ETX);
        } else if (getType() == FRAME_TYPE_EOT) {
            buffer.append(EOT);
        } else if (getType() == FRAME_TYPE_ORDER) {
            StringBuffer bufferForChecksum = new StringBuffer();

            //We have multiple RS and one single GS for the checksum
            //bufferForChecksum.append(ASCII.RS);
            bufferForChecksum.append(String.valueOf(getData()));
            bufferForChecksum.append(ASCII.GS);
            char[] checkSum = computeChecksum(bufferForChecksum);

            buffer.append(ASCII.STX);
            buffer.append(bufferForChecksum);
            buffer.append(checkSum[0]);
            buffer.append(checkSum[1]);
            //		buffer.append(ASCII.CR);
            setDataWithFrame(buffer);
        }
    }

    @Override
    public void extractDataFromFrame() throws Exception {
        super.extractDataFromFrame();
		/*
		StringBuffer dataWithFrame = getDataWithFrame();
		if (dataWithFrame.length() > 0) {
			String str = dataWithFrame.toString();

			if (str.startsWith(String.valueOf(AstmFrame.ENQ))) {
				setType(AstmFrame.FRAME_TYPE_ENQ);
			} else if (str.startsWith(String.valueOf(AstmFrame.EOT))) {
				setType(AstmFrame.FRAME_TYPE_EOT);
			} else if (str.startsWith(String.valueOf(AstmFrame.ETX))) {
				setType(AstmFrame.FRAME_TYPE_LAST);				
			} else if (str.startsWith(String.valueOf(AstmFrame.ACK))) {
				setType(AstmFrame.FRAME_TYPE_ACK);
			} else if (str.startsWith(String.valueOf(AstmFrame.NACK))) {
				setType(AstmFrame.FRAME_TYPE_NACK);
			} else {
				setType(AstmFrame.FRAME_TYPE_RESULT);
				StringBuffer data = new StringBuffer(dataWithFrame.substring(2, dataWithFrame.length() - 3));
				setData(data);
				
				// Verifying the checksum
				StringBuffer bufferForChecksum = new StringBuffer(dataWithFrame.substring(1, dataWithFrame.length() - 3));
				char[] realCheckSum = new char[2];
				realCheckSum[0] = dataWithFrame.charAt(dataWithFrame.length() - 2);
				realCheckSum[1] = dataWithFrame.charAt(dataWithFrame.length() - 1);

				char[] computedCheckSum = computeChecksum(bufferForChecksum);

				if (realCheckSum[0] != computedCheckSum[0] || realCheckSum[1] != computedCheckSum[1]) {
					logString("buffer that was checked:" + bufferForChecksum);
					throw new L3CheckSumException("Checksum exception: expected:" + String.valueOf(computedCheckSum) + " found:" + String.valueOf(realCheckSum));
				} else {
					// logString("Checksum FINE: expected:"+String.valueOf(computedCheckSum)+" found:"+String.valueOf(realCheckSum));
				}
				// ----------------------
			}
		}
		*/
    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        return super.extractAnswerFromBuffer(buffer);
		/*
		String str = buffer.toString();

		boolean extractionDone = false;

		AstmParams params = getAstmParams();
		char lastChar = str.charAt(str.length() - 1);
	
		String eot = String.valueOf(EOT);
		int eotIdx = str.lastIndexOf(eot);
		if (eotIdx >= 0) {
			buffer.replace(0, eotIdx + 1, "");
			extractionDone = true;
			setDataWithFrame(new StringBuffer(String.valueOf(EOT)));
		} else if (lastChar == AstmFrame.ENQ || lastChar == AstmFrame.ACK || lastChar == AstmFrame.NACK) {
			buffer.replace(0, str.length(), "");
			extractionDone = true;
			setDataWithFrame(new StringBuffer(String.valueOf(lastChar)));
		} else {
			String start = String.valueOf(STX);
			String end = String.valueOf(ASCII.GS);
			int sIdx = str.lastIndexOf(start);
			int eIdx = -1;
			if (sIdx >= 0) {
				eIdx = str.lastIndexOf(end);
			}
			if (eIdx > 0 && eIdx > sIdx && eIdx+3 < buffer.length()) {
				StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 3));
				setDataWithFrame(response);
				buffer.replace(0, eIdx + 3, "");
				extractionDone = true;
			}
		}
		return extractionDone;
		*/
    }

}