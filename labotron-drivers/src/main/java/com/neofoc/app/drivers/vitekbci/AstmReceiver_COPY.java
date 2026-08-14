/*
 * Created on Jun 14, 2006
 */
package com.neofoc.app.drivers.vitekbci;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.util.ASCII;
import com.neofoc.app.connection.L3SerialPortListener;
import com.neofoc.app.driver.L3Frame;
import com.neofoc.app.drivers.astm.*;
import com.neofoc.app.exceptions.L3UnexpectedFrameSequenceException;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.util.StringTokenizer;

/**
 * @author 01Barmaja
 */
public class AstmReceiver_COPY implements L3SerialPortListener {
    protected AstmDriver driver = null;
    protected L3Message message = null;
    protected FocLabSample sample = null;
    protected FocLabTest test = null;

    private int expectedSequence = -1;

    private AstmFrame ackFrame = null;
    private AstmFrame nackFrame = null;

    private PatientLineReader patientLineReader = null;
    private OrderLineReader orderLineReader = null;
    private CommentLineReader commentLineReader = null;
    private ResultLineReader resultLineReader = null;
    private CommentResultReader commentResultReader = null;

    private StringBuffer concatenationBuffer = null;

    private int previousAnswer = 0;
    private String previousFrame = null;
    private char lastFrameTypeBeforeComment = AstmFrame.FRAME_TYPE_NONE;

    public AstmReceiver_COPY(AstmDriver driver) {
        this.driver = driver;
        message = null;
        sample = null;
        test = null;

        patientLineReader = new PatientLineReader();
        orderLineReader = new OrderLineReader(driver.getAstmParams().getSampleID_FieldPosition(), driver.getAstmParams().getSampleID_ComponentPosition());
        commentLineReader = new CommentLineReader();
        resultLineReader = new ResultLineReader(driver);
        commentResultReader = new CommentResultReader();
    }

    public void disposeMessage() {
        if (message != null) {
            message.dispose();
        }
        message = null;
        sample = null;
        test = null;

        disposePreviousMessage();
    }

    public void disposePreviousMessage() {
        previousFrame = null;
    }

    public void disposeConcatenationBuffer() {
        concatenationBuffer = null;
    }

    public void dispose() {
        driver = null;

        disposeMessage();
        disposeConcatenationBuffer();

        if (ackFrame != null) {
            ackFrame.dispose();
            ackFrame = null;
        }

        if (nackFrame != null) {
            nackFrame.dispose();
            nackFrame = null;
        }

        if (patientLineReader != null) {
            patientLineReader.dispose();
            patientLineReader = null;
        }

        if (orderLineReader != null) {
            orderLineReader.dispose();
            orderLineReader = null;
        }

        if (commentLineReader != null) {
            commentLineReader.dispose();
            commentLineReader = null;
        }

        if (resultLineReader != null) {
            resultLineReader.dispose();
            resultLineReader = null;
        }

        if (commentResultReader != null) {
            commentResultReader.dispose();
            commentResultReader = null;
        }

    }

    public AstmDriver getDriver() {
        return driver;
    }

    public void setResultLineReader(ResultLineReader resultLineReader) {
        this.resultLineReader = resultLineReader;
    }

    private StringBuffer getConcatenationBuffer() {
        if (concatenationBuffer == null) {
            concatenationBuffer = new StringBuffer();
        }
        return concatenationBuffer;
    }

    private AstmFrame getAckFrame() {
        if (ackFrame == null) {
            ackFrame = AstmFrame.newAcknowlegeFrame(driver.getInstrument());
            try {
                ackFrame.createDataWithFrame();
            } catch (Exception e) {
                ackFrame.logException(e);
            }
        }
        return ackFrame;
    }

    private AstmFrame getNackFrame() {
        if (nackFrame == null) {
            nackFrame = AstmFrame.newNotAcknowlegFrame(driver.getInstrument());
            try {
                nackFrame.createDataWithFrame();
            } catch (Exception e) {
                nackFrame.logException(e);
            }
        }
        return nackFrame;
    }

    private void setExpectedSequence(int sequence) {
        expectedSequence = sequence;
    }

    private void resetExpectedSequence() {
        Globals.logString("RESETTING the Increment");
        expectedSequence = -1;
    }

    private int getNextExpectedSequence() {
        Globals.logString("Increment get next was =" + expectedSequence);
        int next = expectedSequence + 1;
        if (next == 8)
            next = 0;
        Globals.logString("Increment get next next =" + next);
        return next;
    }

    private int incrementExpectedSequence() {
        Globals.logDetail("incrementExpectedSequence() from:" + expectedSequence + " to +1");
        expectedSequence++;
        if (expectedSequence == 8)
            expectedSequence = 0;
        return expectedSequence;
    }

    protected void parsePatientFrame(StringBuffer data) {
        patientLineReader.scanTokens(data);
    }

    protected FocLabTest addTest(FocLabSample sample, String lisTestCode) {
        FocLabTest test = sample.addTest();
        test.setLabel(lisTestCode);
        return test;
    }

    protected void parseOrderFrame(StringBuffer data) {
        orderLineReader.scanTokens(data);

        Globals.logDebug("- Parsed sample ID : " + orderLineReader.getSampleId());
        if (sample != null) {
            Globals.logDebug("- Current sample ID : " + sample.getId());

            // if(sample != null && sample.getId() != null &&
            // orderLineReader.getSampleId() != null ){
            if (sample.getSampleId().compareTo(orderLineReader.getSampleId()) != 0) {
                sample = null;
                sample = message.findSample(orderLineReader.getSampleId());
                if (sample != null) {
                    Globals.logDebug("- Found sample ID : " + sample.getId());
                } else {
                    Globals.logDebug("- sample ID Not Found ");
                }
                test = null;
            }
        }
        if (sample == null) {
            sample = new FocLabSample(orderLineReader.getSampleId());
            sample.setFirstName(patientLineReader.getFirstName());
            sample.setLastName(patientLineReader.getLastName());
            sample.setMiddleInitial(patientLineReader.getMidInitial());
            message.addSample(sample);
        }

        String lisTestCode = driver.testMaps_getLisCode(orderLineReader.getTestLabel());
        if (lisTestCode != null && lisTestCode.trim().compareTo("") != 0) {
            if (driver.getAstmParams().isCheckResultFrameTestCodeWithOrderFrameTestCode()) {
                test = addTest(sample, lisTestCode);
            }
        }
    }

    private void pushCommentAsMessage(String comment) {
        // Il faudrait le faire ds getSqlString() plutot
        if (comment != null && comment.contains("\'")) {
            int idxQuote = comment.indexOf('\'');
            String str = comment.substring(0, idxQuote + 1) + "\'" + comment.substring(idxQuote + 1);
            comment = str;
        }
        if (comment != null && !comment.isEmpty()) {
            sample.pushMessage(getDriver().getInstrument(), comment);
        }
    }

    protected void parseCommentFrame(StringBuffer data) {
        if (driver.getAstmParams().isReadAnyComment()) {
            commentLineReader.scanTokens(data);

            if (getDriver().getAstmParams().isReadComment1()) {
                String comment1 = commentLineReader.getComment1();
                pushCommentAsMessage(comment1);
            }

            if (getDriver().getAstmParams().isReadComment2()) {
                String comment2 = commentLineReader.getComment2();
                pushCommentAsMessage(comment2);
            }

            if (getDriver().getAstmParams().isReadComment3()) {
                String comment3 = commentLineReader.getComment3();
                pushCommentAsMessage(comment3);
            }

            /*
             * if(comment3.isEmpty()) comment3 =
             * commentLineReader.getComment1(); // Il faudrait le faire ds
             * getSqlString() plutot if (comment3!=null &&
             * comment3.contains("\'")) { int idxQuote = comment3.indexOf('\'');
             * String str =
             * comment3.substring(0,idxQuote+1)+"\'"+comment3.substring
             * (idxQuote+1); comment3 = str; }
             * sample.pushMessage(getDriver().getInstrument(), comment3);
             */
        }
    }

    protected void parseResultFrame(StringBuffer data) {
        resultLineReader.setSample(sample);
        resultLineReader.setTest(test);
        resultLineReader.scanTokens(data);
        test = resultLineReader.getTest();
    }

    protected void parseCommentAfterResultFrame(StringBuffer data) {
        if (driver.getAstmParams().isReadResultComment()) {
            commentResultReader.scanTokens(data);
            char alarmCode = commentResultReader.getCommentOnResult();
            if (test != null && alarmCode != 0) {
                String alarmMsg = "" + alarmCode;
                if (!test.getNotificationMessage().equals("")) {
                    alarmMsg = test.getNotificationMessage() + ", " + alarmCode;
                }
                test.setNotificationMessage(alarmMsg);
            }

        }
    }

    protected void initMessage() {
        message = new L3Message();
    }

    protected void sendMessageBackToInstrument() {
        driver.notifyListeners(message);
    }

    protected void treatResultFrame(AstmFrame frame) {
        StringBuffer data = frame.getData();

        Globals.logString("Inside treatResultFrame() frameType=" + frame.getType());

        if (frame.getType() == AstmFrame.FRAME_TYPE_HEADER) {
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_LAST) {
            // sendMessageBackToInstrument();
            // disposeMessage();
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_PATIENT) {
            // initMessage();
            parsePatientFrame(data);
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_ORDER) {
            parseOrderFrame(data);
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_RESULT) {
            parseResultFrame(data);
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_COMMENT) {
            if (lastFrameTypeBeforeComment == AstmFrame.FRAME_TYPE_ORDER) {
                parseCommentFrame(data);
            } else if (lastFrameTypeBeforeComment == AstmFrame.FRAME_TYPE_RESULT) {
                parseCommentAfterResultFrame(data);
            }

        }

        if (frame.getType() != AstmFrame.FRAME_TYPE_COMMENT) {
            lastFrameTypeBeforeComment = frame.getType();
        }
    }

    private int executionAfterReceivingENQ_ReserveAndResetSequence() {
        Globals.logString("Inside executionAfterReceivingENQ_ReserveAndResetSequence ");

        int frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
        Globals.logDetail("Receive ENQ");
        if (driver.reserve(true)) {
            driver.getInstrument().logString("Returning NACK because driver is Reserved");
            frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
        } else {
            disposeConcatenationBuffer();
            getConcatenationBuffer();
            resetExpectedSequence();
            getNextExpectedSequence();
            frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
        }
        return frameTypeToReturn;
    }

    private boolean isAcceptToStartWithHeaderDirectly() {
        return getDriver() != null && getDriver().getAstmParams() != null && getDriver().getAstmParams().isAcceptToStartAtFrame1Directly();
    }

    protected int concatenateResultFrame(AstmFrame frame) {
        int frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
        try {
            frame.extractDataFromFrame();

            int expectedSequence = getNextExpectedSequence();
            if (frame.getSequence() != AstmFrame.SEQUENCE_IRRELEVANT && expectedSequence != frame.getSequence()) {
                if (isAcceptToStartWithHeaderDirectly() && frame.getType() == AstmFrame.FRAME_TYPE_HEADER) {
                    frameTypeToReturn = executionAfterReceivingENQ_ReserveAndResetSequence();
                    if (frameTypeToReturn == AstmFrame.FRAME_TYPE_ACK) {
                        setExpectedSequence(frame.getSequence());
                    } else {
                        throw new L3UnexpectedFrameSequenceException("Received Header without ENQ but could not reserve driver!");
                    }
                } else {
                    throw new L3UnexpectedFrameSequenceException("Sequence expected : " + expectedSequence + " found:" + frame.getSequence());
                }
            }

            if (frame.getType() == AstmFrame.FRAME_TYPE_ENQ) {
                frameTypeToReturn = executionAfterReceivingENQ_ReserveAndResetSequence();
//				Globals.logDetail("Receive ENQ");
//				if (driver.reserve()) {
//					driver.getInstrument().logString("Returning NACK because driver is Reserved");
//					frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
//				} else {
//					disposeConcatenationBuffer();
//					getConcatenationBuffer();
//					resetExpectedSequence();
//					getNextExpectedSequence();
//					frameTypeToReturn = AstmFrame.FRAME_TYPE_ACK;
//				}
            } else if (frame.getType() == AstmFrame.FRAME_TYPE_EOT) {
                Globals.logDetail("Receive EOT");
                frameTypeToReturn = AstmFrame.FRAME_TYPE_NONE;

                Globals.logDebug("Concatenation Buffer = " + getConcatenationBuffer());

                disposeMessage();

                StringTokenizer strTokenizer = new StringTokenizer(getConcatenationBuffer().toString(), String.valueOf(ASCII.CR), false);
                while (strTokenizer.hasMoreTokens()) {
                    String token = strTokenizer.nextToken();
                    if (ConfigInfo.isLogDebug()) {
                        Globals.logDebug("Concatenated Frame : " + ASCII.convertNonCharactersToDescriptions(token));
                    }
                    AstmFrame concatFrame = new AstmFrame(driver.getInstrument());
                    if (token.length() > 0) {
                        concatFrame.setDataWithFrame(new StringBuffer(token));
                        concatFrame.extractDataFromConcatenatedFrame();
                        if (message == null) {
                            initMessage();
                        }
                        treatResultFrame(concatFrame);
                    }
                }
                sendMessageBackToInstrument();
                disposeMessage();
                disposeConcatenationBuffer();

                driver.release();

            } else {
                StringBuffer sb = getConcatenationBuffer();
                frame.concatenateToBuffer(sb);
            }
        } catch (Exception e) {
            driver.getInstrument().logException(e);
            frameTypeToReturn = AstmFrame.FRAME_TYPE_NACK;
        }

        return frameTypeToReturn;
    }

    /*
     * (non-Javadoc)
     *
     * @see b01.l3.connection.L3SerialPortListener#received(b01.l3.L3Frame)
     */
    public void received(L3Frame frame) {
        try {
            Globals.logString("received()");
            AstmFrame f = (AstmFrame) frame;
            if (f != null) {
                String newFrameStr = f.getDataWithFrame().toString();
                Globals.logString("received frame:" + newFrameStr);
                int answer = AstmFrame.FRAME_TYPE_NONE;
                boolean repeatedFrame = false;

                if (previousFrame != null && newFrameStr.toString().equals(previousFrame)) {
                    Globals.logString("Equal to previous");
                    answer = previousAnswer;
                    if (answer == AstmFrame.FRAME_TYPE_NACK) {
                        driver.getInstrument().logString("Sending NACK because received a repeated message");
                    }
                    repeatedFrame = true;
                } else {
                    Globals.logString("Concatenating the ResultFrame");
                    answer = concatenateResultFrame(f);
                    Globals.logString("Getting this answer" + answer);
                }

                switch (answer) {
                    case AstmFrame.FRAME_TYPE_ACK:
                        Globals.logString("Inside CASE ACK");
                        if (!repeatedFrame) {
                            Globals.logString("Calling the increment");
                            incrementExpectedSequence();
                        }

                        if (!f.isDoNotAnswerThatFrameBecauseReceivedAnotherMessage()) {
                            driver.send(getAckFrame().getDataWithFrame().toString());
                        } else {
                            Globals.logString("Was Sending : " + AstmFrame.ACK);
                        }
                        break;
                    case AstmFrame.FRAME_TYPE_NACK:
                        Globals.logString("Inside CASE NACK");
                        if (!f.isDoNotAnswerThatFrameBecauseReceivedAnotherMessage()) {
                            driver.getInstrument().logString("sending NACK from AstmReceiver.received(L3Frame frame)");
                            driver.send(getNackFrame().getDataWithFrame().toString());
                        } else {
                            Globals.logString("Was Sending : " + AstmFrame.NACK);
                        }
                        break;
                    case AstmFrame.FRAME_TYPE_NONE:
                        Globals.logString("Inside CASE NONE");
                        break;
                }

                previousFrame = newFrameStr;
                previousAnswer = answer;
                // If the previous frame was ENQ and the previous answer is NACK
                // we do not repeat the answer without checking driver
                // availability.
                // Otherwise we get stuck
                if (f.getType() == AstmFrame.FRAME_TYPE_ENQ && previousAnswer == AstmFrame.FRAME_TYPE_NACK) {
                    previousFrame = null;
                    previousAnswer = 0;
                }
            }
        } catch (Exception e) {
            Globals.logException(e);
        }
    }

}