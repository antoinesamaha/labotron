package com.neofoc.app.drivers.astm;

import com.foc.Globals;
import com.foc.util.ASCII;
import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.driver.L3ConfigInfo;
import com.neofoc.app.driver.L3Frame;
import com.neofoc.app.exceptions.*;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.util.Date;
import java.util.Properties;

public class AstmDriver extends DriverSerialPort {
	public static int MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS = 3;
	private AstmParams astmParams = null;
	protected AstmFrameCreator frameCreator = null;
	private boolean receivedENQWhileSendingENQ = false;

	public AstmDriver() {
		super();
		frameCreator = new AstmFrameCreator();
		astmParams = new AstmParams();
	}

	protected void initDriverReceiver() {
		setDriverReceiver(new AstmReceiver(this));
	}

	protected void initAnswerFrame() {
		AstmFrame answerFrame = new AstmFrame(getInstrument());
		getL3SerialPort().setAnswerFrame(answerFrame);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#init(java.util.Properties)
	 */
	public void init(FocInstrument instrument, Properties props) throws Exception {
		if (L3ConfigInfo.getDebugMode()) {
			Globals.getDisplayManager().popupMessage("01BARMAJA you are in Debug MODE !!!!");
			// RESPONSE_TIMEOUT = 1000 * 60 * 5 ;
		}
		initDriverReceiver();
		super.init(instrument, props);

		getInstrument().logString("L3 date : " + (new Date(System.currentTimeMillis())).toString());
		newSerialPort(props);

		testMaps_fillFromProperties(props);
		initAnswerFrame();
	}

	public AstmParams getAstmParams() {
		return astmParams;
	}
	
	@Override
	protected boolean releaseWhenReceiveENQ(){
		return getAstmParams().isReleaseWhenReceivedENQ() && !getL3SerialPort().isSynchronousMessage();
	}
	
	@Override
	protected void releasing() {
		super.releasing();
	}

	@Override
	protected void synchronizeSequenceID() throws Exception {
	}

	public void send(L3Message message) throws Exception {
		//20160129-B
		if(getAstmParams() == null || !getAstmParams().isDoNotSendOrdersBecauseOneWay()){
		//20160129-E
			if (getInstrument().isLogBufferDetails()) {
				getInstrument().logString("Inst code : " + message.sampleIterator());
			}
			resetFrameArray();
			frameCreator.buildFrameArray(this, message, true);
			sendFramesArray(true);
		//20160129-B			
		}
		//20160129-E
	}

	public void sendFramesArray(boolean createDataWithFrame) throws Exception {
		if (createDataWithFrame) {
			for (int i = 0; i < getFrameCount(); i++) {
				L3Frame frame = getFrameAt(i);
				if (frame != null) {
					frame.createDataWithFrame();
					// getInstrument().logString(frame.getDataWithFrame());
				}
			}
		}

		int timeOutDelay = getInstrument().getPropertyInteger("delay_driver_time_out_for_response");

		int i = 0;
		int numberOfFailures = 0;
		for (i = 0; i < getFrameCount() && numberOfFailures < MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS; i++) {
			AstmFrame frame = (AstmFrame) getFrameAt(i);
			if (frame != null) {

				// Sleep 1 second before resending. Done following the problem
				// at Cobas C511.
				if (numberOfFailures > 0) {
					try {
						Thread.sleep(1000);
					} catch (Exception e) {
						if (getInstrument() != null) {
							getInstrument().logException(e);
						}
					}
				}

				String strToSend = frame.getDataWithFrame().toString();
				AstmFrame resFrame = null;
				if (frame.getType() == AstmFrame.FRAME_TYPE_EOT) {
					getL3SerialPort().send(strToSend);
				} else {
					boolean firstTry = true;
					try {
						resFrame = (AstmFrame) getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
					} catch (L3TimeOutException e) {
						getInstrument().logException(e);
						if (i == 0) {
							throw new L3InstrumentDoesNotRespondTryLaterException();
						}
						if (firstTry) {
							Globals.logString("SECOND TRY AFTER TIME OUT");
							resFrame = (AstmFrame) getL3SerialPort().sendAndWaitForResponse(strToSend, timeOutDelay);
							firstTry = false;
						} else {
							throw e;
						}
					} catch (L3UnexpectedFrameSequenceException e) {
						if (i == 0) {
							throw new L3InstrumentDoesNotRespondTryLaterException();
						} else {
							throw e;
						}
					}

					resFrame.extractDataFromFrame();
					char type = resFrame.getType();
					switch (type) {
					case AstmFrame.FRAME_TYPE_ENQ:
						if (isInquiryBased()) {
							//In CS2500 Inquiry based the answer to ENQ is ENQ according to support							
							numberOfFailures = 0;
							break;
						} else {
							if (getAstmParams().isSlaveBehaviour()) {
								setReceivedENQWhileSendingENQ(true);
							}
							throw new L3TryLaterException();// This is only thrown
															// when we receive an
															// ENQ while sending
															// info to the machine
						}
					case AstmFrame.FRAME_TYPE_ACK:
						numberOfFailures = 0;
						break;
					case AstmFrame.FRAME_TYPE_NACK:
						numberOfFailures++;
						i--;
						break;
					/*
					 * case AstmFrame.FRAME_TYPE_HEADER:
					 * if(getAstmParams().isSlaveBehaviour()){
					 * setReceivedENQWhileSendingENQ(true); break;//The break is
					 * inside the if because otherwise we want the Default to
					 * hapen }
					 */
					default:// case AstmFrame.FRAME_TYPE_HEADER: should be right
							// before the default.
						if (i == 0) {
							getInstrument().logString("Wrong answer for ENQ the driver will try to send later.");
							throw new L3InstrumentDoesNotRespondTryLaterException();
						} else {
							throw new L3UnexpectedFrameTypeException("Unexpected answer frame type: " + resFrame.getDataWithFrame() + "\n For request " + frame.getDataWithFrame());
						}
					}
				}
			}
		}

		if (numberOfFailures == MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS) {
			throw new L3AnalyzerErrorReturnException("Frame rejected " + MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS + " times :" + getFrameAt(i).getDataWithFrame());
		}
	}

	public boolean isResendAllPendingTests() {
		return false;
	}

	@Override
	public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
		// TODO Auto-generated method stub

	}

	@Override
	public synchronized void release() {
		super.release();
		if (isReceivedENQWhileSendingENQ()) {
			getInstrument().logString("Driver received ENQ While Sending ENQ. Will have a slave behaviour.");
			setReceivedENQWhileSendingENQ(false);
			((AstmReceiver) getDriverReceiver()).disposePreviousMessage();
			getL3SerialPort().extractAnswerFromBuffer(new StringBuffer(String.valueOf(ASCII.ENQ)));
		}
	}

	public boolean isReceivedENQWhileSendingENQ() {
		return receivedENQWhileSendingENQ;
	}

	public void setReceivedENQWhileSendingENQ(boolean receivedENQWhileSendingENQ) {
		this.receivedENQWhileSendingENQ = receivedENQWhileSendingENQ;
	}
}