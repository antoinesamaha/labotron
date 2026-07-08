package com.neofoc.app.drivers.astm;

import com.foc.Globals;
import com.foc.util.ASCII;
import com.foc.util.Int2ByteConverter;
import com.neofoc.app.driver.L3Frame;
import com.neofoc.app.exceptions.L3CheckSumException;
import com.neofoc.app.exceptions.L3SequenceNotAssignedException;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class AstmFrame extends L3Frame {
	private char type = FRAME_TYPE_ENQ;
	private int sequence = SEQUENCE_NOT_ASSIGNED;
	private boolean intermediateFrame = false;

	public final static int SEQUENCE_IRRELEVANT = -10;
	public final static int SEQUENCE_NOT_ASSIGNED = -1;

	public static final char ENQ = 5;
	public static final char ACK = 6;
	public static final char NACK = 21; // 15 pour Pentra (project astm)
	public static final char EOT = 4;
	public static final char SINGLE_CHAR_NOT_FOUND = 0;

	public static final char SOH = 1;
	public static final char STX = 2;
	public static final char ETB = 23;
	public static final char ETX = 3;
	public static final char CR = 13;
	public static final char LF = 10;
	public static final char VT = ASCII.VT;
	public static final char FS = ASCII.FS;

	public final static char FRAME_TYPE_HEADER = 'H';
	public final static char FRAME_TYPE_PATIENT = 'P';
	public final static char FRAME_TYPE_ORDER = 'O';
	public final static char FRAME_TYPE_INFORMATION_INQUIRY = 'Q';	
	public final static char FRAME_TYPE_RESULT = 'R';
	public final static char FRAME_TYPE_LAST = 'L';
	public final static char FRAME_TYPE_COMMENT = 'C';
	public final static char FRAME_TYPE_NONE = '-';
	// public final static char FRAME_TYPE_M = 'M';

	public final static char FRAME_TYPE_ENQ = 'e';
	public final static char FRAME_TYPE_ACK = 'a';
	public final static char FRAME_TYPE_NACK = 'n';
	public final static char FRAME_TYPE_EOT = 't';
	public final static char FRAME_TYPE_CONTINUITY = 'X';

	public static final char FIELD_SEPERATOR = '|';
	public static final char COMPONENT_DELIMITER = '^';
	public static final char ESCAPE_DELIMITER = '&';
	public static final char REPEAT_DELIMITER = '\\';// 92
	public static final char REPEAT_SUB_FIELD_DELIMITER = '~';

	public AstmFrame(FocInstrument instrument) {
		super(instrument);
		this.type = FRAME_TYPE_ENQ;
	}

	public AstmFrame(FocInstrument instrument, int sequence, char type) {
		super(instrument);
		this.sequence = sequence;
		this.type = type;
	}

	public void dispose() {
		super.dispose();
	}

	// private AstmDriver getDriver() throws Exception {
	// return (AstmDriver) getInstrument().getDriver();
	// }

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public char getType() {
		return type;
	}

	public void setType(char type) {
		this.type = type;
	}

	public static AstmFrame newAcknowlegeFrame(FocInstrument instrument) {
		AstmFrame frame = new AstmFrame(instrument, SEQUENCE_IRRELEVANT, FRAME_TYPE_ACK);
		return frame;
	}

	public static AstmFrame newNotAcknowlegFrame(FocInstrument instrument) {
		AstmFrame frame = new AstmFrame(instrument, SEQUENCE_IRRELEVANT, FRAME_TYPE_NACK);
		return frame;
	}

	public AstmParams getAstmParams() {
		AstmParams params = null;
		FocInstrument instr = getInstrument();
		if (instr != null) {
			try {
				AstmDriver driver = (AstmDriver) instr.getDriver();
				params = driver.getAstmParams();
			} catch (Exception e) {
				Globals.logException(e);
			}

		}
		return params;
	}

	public char[] computeChecksum(StringBuffer strBuffer) {
		char parity[] = new char[2];

		byte bt[] = strBuffer.toString().getBytes();

		int sum = 0;
		for (int i = 0; i < bt.length; i++) {
			// Globals.logString("byte ="+bt[i]+" char="+ct[i]);
			sum += bt[i];
			// Globals.logString(i+" "+c+" "+bt[i]);
		}
		int mod = sum % 256;

		Int2ByteConverter conv = new Int2ByteConverter(mod);
		parity[0] = conv.getHighByte();
		parity[1] = conv.getLowByte();

		// Globals.logString("parity: " + mod+" -> "+strBuffer);
		return parity;
	}

	@Override
	public void createDataWithFrame() throws Exception {
		StringBuffer buffer = new StringBuffer();

		if (type == FRAME_TYPE_ENQ) {
			buffer.append(ENQ);
		} else if (type == FRAME_TYPE_ACK) {
			buffer.append(ACK);
		} else if (type == FRAME_TYPE_NACK) {
			buffer.append(NACK);
		} else if (type == FRAME_TYPE_EOT) {
			buffer.append(EOT);
		} else {
			if (sequence < SEQUENCE_NOT_ASSIGNED) {
				throw new L3SequenceNotAssignedException("Sequence not assigned for frame: " + getData());
			}

			buffer.append(STX);

			StringBuffer bufferForChecksum = new StringBuffer();
			bufferForChecksum.append(String.valueOf(sequence));

			if (type != FRAME_TYPE_CONTINUITY && type != FRAME_TYPE_NONE) {
				bufferForChecksum.append(type);
			}
			bufferForChecksum.append(String.valueOf(getData()));

			if (type != FRAME_TYPE_CONTINUITY) {
				bufferForChecksum.append(CR);
			}
			if (isIntermediateFrame()) {
				bufferForChecksum.append(ETB);
			} else {
				bufferForChecksum.append(ETX);
			}

			char[] checkSum = computeChecksum(bufferForChecksum);

			buffer.append(bufferForChecksum);
			buffer.append(checkSum[0]);
			buffer.append(checkSum[1]);
			buffer.append(CR);
			buffer.append(LF);
		}
		setDataWithFrame(buffer);
	}

	public void extractDataFromConcatenatedFrame() {
		StringBuffer dataWithFrame = getDataWithFrame();
		if (dataWithFrame.length() > 1) {
			// Only the leading record-type character (captured below) belongs to the
			// frame header. Concatenated records are already split on CR with no
			// trailing overhead byte, so nothing should be trimmed off the end here.
			StringBuffer data = new StringBuffer(dataWithFrame.substring(1));
			setData(data);
		}
		type = dataWithFrame.charAt(0);
	}

	@Override
	public void extractDataFromFrame() throws Exception {
		StringBuffer dataWithFrame = getDataWithFrame();
		if (dataWithFrame.length() > 0) {
			String str = dataWithFrame.toString();

			if (str.startsWith(String.valueOf(AstmFrame.ENQ))) {
				setType(AstmFrame.FRAME_TYPE_ENQ);
				sequence = SEQUENCE_IRRELEVANT;
			} else if (str.startsWith(String.valueOf(AstmFrame.EOT))) {
				setType(AstmFrame.FRAME_TYPE_EOT);
				sequence = SEQUENCE_IRRELEVANT;
			} else if (str.startsWith(String.valueOf(AstmFrame.ACK))) {
				setType(AstmFrame.FRAME_TYPE_ACK);
				sequence = SEQUENCE_IRRELEVANT;
			} else if (str.startsWith(String.valueOf(AstmFrame.NACK))) {
				setType(AstmFrame.FRAME_TYPE_NACK);
				sequence = SEQUENCE_IRRELEVANT;
			} else {
				boolean keepTheLastChar = false;
				if (getInstrument() != null && getInstrument().getDriver() != null) {
					AstmDriver driver = (AstmDriver) getInstrument().getDriver();
					keepTheLastChar = driver.getAstmParams().isConcatenatedFrames();
				}
				int minus = 6;
				if (keepTheLastChar)
					minus = 5;

				if (dataWithFrame.length() > (3 + minus)) {
					StringBuffer data = new StringBuffer(dataWithFrame.substring(3, dataWithFrame.length() - minus));
					setData(data);
				} else {
					//The Frame is empty maybe sent for connectivity testing. Seen in the case of GEM Premier 3500. We will not throw an exception but we will log it.
				}


				sequence = Integer.valueOf((String) dataWithFrame.substring(1, 2)).intValue();
				type = dataWithFrame.charAt(2);

				// Verifying the checksum
				StringBuffer bufferForChecksum = new StringBuffer(dataWithFrame);
				bufferForChecksum.replace(0, 1, "");
				bufferForChecksum.replace(dataWithFrame.length() - 5, dataWithFrame.length(), "");
				char[] realCheckSum = new char[2];
				realCheckSum[0] = dataWithFrame.charAt(dataWithFrame.length() - 4);
				realCheckSum[1] = dataWithFrame.charAt(dataWithFrame.length() - 3);

				char[] computedCheckSum = computeChecksum(bufferForChecksum);

				if (realCheckSum[0] != computedCheckSum[0] || realCheckSum[1] != computedCheckSum[1]) {
					logString("buffer that was checked:" + bufferForChecksum);
					throw new L3CheckSumException("Checksum exception: expected:" + String.valueOf(computedCheckSum) + " found:" + String.valueOf(realCheckSum));
				} else {
					// logString("Checksum FINE: expected:"+String.valueOf(computedCheckSum)+" found:"+String.valueOf(realCheckSum));
				}
				// ----------------------

				AstmParams params = getAstmParams();
				if (params != null && params.isConcatenatedFrames()) {
					StringBuffer theData = getData();
					theData.insert(0, type);
					type = AstmFrame.FRAME_TYPE_CONTINUITY;
				}
			}
		}
	}

	public void concatenateToBuffer(StringBuffer buffer) {
		if (!getAstmParams().isConcatenatedFrames()) {
			buffer.append("" + getType() + getData());
			buffer.append(ASCII.CR);
		} else {
			buffer.append(getData());
		}
	}

	@Override
	public boolean extractAnswerFromBuffer(StringBuffer buffer) {
		String str = buffer.toString();

		boolean extractionDone = false;

		AstmParams params = getAstmParams();
		if (params != null && params.isTakeAllFramesFromBufferNotJustTheLast()) {
			CharWithPosition charWithPos = new CharWithPosition();			
			charWithPos.find(str, AstmFrame.ENQ);
			charWithPos.find(str, AstmFrame.ACK);
			charWithPos.find(str, AstmFrame.NACK);
			charWithPos.find(str, AstmFrame.EOT);
			charWithPos.find(str, AstmFrame.STX, AstmFrame.LF);

			if (charWithPos.isFound()){
				
				switch (charWithPos.getChar()) {
				case AstmFrame.ENQ:
				case AstmFrame.ACK:
				case AstmFrame.NACK: {
					buffer.replace(0, charWithPos.getPos()+1, "");
					if(charWithPos.getChar() == ENQ){//If it is an ENQ we want to remove all the following ENQs
						while(buffer.length() > 0 && buffer.charAt(0) == ENQ){
							buffer.replace(0, 1, "");
						}
					}
					extractionDone = true;
					setDataWithFrame(new StringBuffer(String.valueOf(charWithPos.getChar())));
				}
					break;
				case AstmFrame.EOT: {
					buffer.replace(0, charWithPos.getPos()+1, "");
					extractionDone = true;
					setDataWithFrame(new StringBuffer(String.valueOf(EOT)));
				}
					break;
				case AstmFrame.STX: {
					int sIdx = charWithPos.getPos();
					int eIdx = str.indexOf(LF, charWithPos.getPos());
					if (eIdx > 0 && eIdx > sIdx) {
						StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 1));
						setDataWithFrame(response);
						buffer.replace(0, eIdx + 1, "");
						extractionDone = true;
					}
				}
					break;
				}
				
				setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(false);
				
				//If we read the buffer and there is still characters in the buffer this means that we 
				//do not need to answer that buffer because we already received a following message 
				//and we only need to answer the following one.
				if(extractionDone && buffer.length() > 0){
					Globals.logString("Message has been read Remaining In Buffer : " + buffer.toString());
					setDoNotAnswerThatFrameBecauseReceivedAnotherMessage(true);
				}else{
					Globals.logString("Message has been read nothing In Buffer");
				}
			}
		} else {
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
				String end = String.valueOf(LF);
				int sIdx = str.lastIndexOf(start);
				int eIdx = -1;
				if (sIdx >= 0) {
					eIdx = str.lastIndexOf(end);
				}
				if (eIdx > 0 && eIdx > sIdx) {
					StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 1));
					setDataWithFrame(response);
					buffer.replace(0, eIdx + 1, "");
					extractionDone = true;
				}
			}
		}
		return extractionDone;
	}

	public boolean isIntermediateFrame() {
		return intermediateFrame;
	}

	public void setIntermediateFrame(boolean intermediateFrame) {
		this.intermediateFrame = intermediateFrame;
	}

	public class CharWithPosition {
		private int pos = -1;
		private char c;
		private boolean found = false;

		public CharWithPosition() {
		}

		public boolean find(String str, char c) {
			boolean found = find(str, c, (char)0);
			return found;
		}
		
		public boolean find(String str, char c, char e){
			boolean found = false;

			int newIndex = str != null ? str.indexOf(c) : -1;
			if (newIndex >= 0 && (pos < 0 || newIndex < pos)) {
				int endIndex = (e > 0) ? str.indexOf(e, newIndex) : 99999;
				if(endIndex > 0){
					this.pos = newIndex;
					this.c = c;
					this.found = true;
				}
			}

			return found;
		}

		public int getPos() {
			return pos;
		}

		public char getChar() {
			return c;
		}

		public boolean isFound() {
			return found;
		}
	}
}
