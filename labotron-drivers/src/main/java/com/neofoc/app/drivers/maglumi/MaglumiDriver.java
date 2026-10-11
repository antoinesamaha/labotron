package com.neofoc.app.drivers.maglumi;

import com.neofoc.app.driver.L3Frame;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.exceptions.L3AnalyzerErrorReturnException;
import com.neofoc.app.exceptions.L3InstrumentDoesNotRespondTryLaterException;
import com.neofoc.app.exceptions.L3TimeOutException;
import com.neofoc.app.exceptions.L3TryLaterException;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class MaglumiDriver extends AstmDriver {

    // The EOT is ACKed per the specification, but we do not want to block long if it is not
    private static final int EOT_ACK_TIMEOUT = 2000;

    public MaglumiDriver() {
        super();
        if (frameCreator != null) {
            frameCreator.dispose();
            frameCreator = null;
        }
        frameCreator = new MaglumiFrameCreator();

        getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
        // Maglumi assay names are longer than 3 chars (17a-OH P, CA125, CYFRA211). Spec max is 10.
        getAstmParams().setTestCodeLength(10);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setConcatenatedFrames(true);
        getAstmParams().setReadComment3(true);
        getAstmParams().setReadResultComment(true);
        getAstmParams().setTreatHigherLessAlarmSeparately(false);
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        if (props != null) {
            props.put("tcpip", "1");
        }
        super.init(instrument, props);
    }

    @Override
    protected void initDriverReceiver() {
        setDriverReceiver(new MaglumiReceiver(this));
    }

    @Override
    protected void initAnswerFrame() {
        getL3SerialPort().setAnswerFrame(new MaglumiFrame(getInstrument()));
    }

    /**
     * Per the Maglumi specification (B.6.2) each step is ACKed by the instrument:
     *   ENQ, STX, H...L records separated by CR, ETX, EOT
     * No frame number, no checksum, no CR LF.
     */
    @Override
    public void sendFramesArray(boolean createDataWithFrame) throws Exception {
        StringBuffer content = new StringBuffer();
        for (int i = 0; i < getFrameCount(); i++) {
            L3Frame frame = getFrameAt(i);
            if (frame != null && frame.getData() != null) {
                content.append(((AstmFrame) frame).getType());
                content.append(frame.getData());
                content.append(AstmFrame.CR);
            }
        }

        int timeOut = getInstrument().getDlyForTimeOut();
        sendENQ(timeOut);
        sendAndExpectAck(String.valueOf(AstmFrame.STX), timeOut);
        sendAndExpectAck(content.toString(), timeOut);
        sendAndExpectAck(String.valueOf(AstmFrame.ETX), timeOut);
        try {
            sendAndExpectAck(String.valueOf(AstmFrame.EOT), Math.min(timeOut, EOT_ACK_TIMEOUT));
        } catch (L3TimeOutException e) {
            getInstrument().logString("Maglumi did not ACK the EOT, message considered sent");
        }
    }

    private void sendENQ(int timeOut) throws Exception {
        AstmFrame resFrame = null;
        try {
            resFrame = (AstmFrame) getL3SerialPort().sendAndWaitForResponse(String.valueOf(AstmFrame.ENQ), timeOut);
        } catch (L3TimeOutException e) {
            getInstrument().logException(e);
            throw new L3InstrumentDoesNotRespondTryLaterException();
        }
        resFrame.extractDataFromFrame();
        switch (resFrame.getType()) {
            case AstmFrame.FRAME_TYPE_ACK:
                break;
            case AstmFrame.FRAME_TYPE_ENQ:
                // Collision: the Maglumi wants to send, we try later
                if (getAstmParams().isYieldOnEnqCollision()) {
                    setReceivedENQWhileSendingENQ(true);
                }
                throw new L3TryLaterException();
            default:
                getInstrument().logString("Wrong answer for ENQ the driver will try to send later.");
                throw new L3InstrumentDoesNotRespondTryLaterException();
        }
    }

    private void sendAndExpectAck(String strToSend, int timeOut) throws Exception {
        for (int attempt = 1; attempt <= MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS; attempt++) {
            AstmFrame resFrame = (AstmFrame) getL3SerialPort().sendAndWaitForResponse(strToSend, timeOut);
            resFrame.extractDataFromFrame();
            char type = resFrame.getType();
            if (type == AstmFrame.FRAME_TYPE_ACK) {
                return;
            } else if (type != AstmFrame.FRAME_TYPE_NACK) {
                throw new L3AnalyzerErrorReturnException("Maglumi unexpected answer: " + resFrame.getDataWithFrame());
            }
            Thread.sleep(1000);
        }
        throw new L3AnalyzerErrorReturnException("Maglumi rejected " + MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS + " times: " + strToSend);
    }
}
