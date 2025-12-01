package com.neofoc.app.drivers.alegria;

import com.foc.Globals;
import com.foc.desc.FocObject;
import com.foc.list.FocListElement;
import com.foc.list.FocListIterator;
import com.neofoc.app.connection.L3SerialPortListener;
import com.neofoc.app.driver.L3Frame;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.util.Iterator;

public class AlegriaReceiver implements L3SerialPortListener {

    private AlegriaDriver driver;
    private L3Message messageSentAsOrderToInstrument;

    private static final int[] PROGRAM_CODE = {2, 1};
    private static final int[] SAMPLE_NUMBER = {3, 4};
    private static final int[] PATIENT_ID = {7, 15};
    private static final int[] PATIENT_NAME = {22, 30};
    private static final int[] PATIENT_DOB = {52, 8};
    private static final int[] PATIENT_SEX = {60, 1};

    private static final int[] SAMPLE_DATE = {84, 8};
    private static final int[] CONCENTRATION = {92, 5};
    private static final int[] CONCENTRATION_UNIT = {97, 8};
    private static final int[] FRACTION_1_NAME = {268, 10};
    private static final int[] FRACTION_1_PERCENT = {368, 5};

    private static final int[] INQUIRY__PATIENT_ID = {3, 15};

    public AlegriaReceiver(AlegriaDriver driver) {

        this.driver = driver;
    }

    public void dispose() {
        dispose_MessageSentAsOrderToInstrument();
        driver = null;
    }

    public void dispose_MessageSentAsOrderToInstrument() {
        if (messageSentAsOrderToInstrument != null) {
            messageSentAsOrderToInstrument.dispose();
            messageSentAsOrderToInstrument = null;
        }
    }

    private void extractDataFromFrame(AstmFrame frame) {
        // This method should parse the frame and extract the relevant information
        // based on the defined indices.
        // For now, we will just log the received frame.
        Globals.logString("OctaReceiver analysing frame: " + frame);
        if (       frame.getDataWithFrame().length() > 2
                && frame.getDataWithFrame().charAt(0) == AstmFrame.STX
                && (   frame.getDataWithFrame().charAt(frame.getDataWithFrame().length() - 1) == AstmFrame.ETX
                || frame.getDataWithFrame().charAt(frame.getDataWithFrame().length() - 1) == AstmFrame.EOT)
        ) {
            StringBuffer data = new StringBuffer(frame.getDataWithFrame().substring(1, frame.getDataWithFrame().length() - 1));
            frame.setData(data);

            if (data.length() > 700) {
                frame.setType(AstmFrame.FRAME_TYPE_RESULT);
            } else if (data.length() == 16) {
                frame.setType(AstmFrame.FRAME_TYPE_INFORMATION_INQUIRY);
            } else if (data.length() > 0 && data.length() < 3) {
                if (data.charAt(0) == AstmFrame.ACK) {
                    frame.setType(AstmFrame.FRAME_TYPE_ACK);
                }else if (data.charAt(0) == AstmFrame.NACK) {
                    frame.setType(AstmFrame.FRAME_TYPE_NACK);
                }
            }
        } else if (frame.getDataWithFrame().length() == 1) {
            if (frame.getDataWithFrame().charAt(0) == AstmFrame.ENQ) {
                frame.setType(AstmFrame.FRAME_TYPE_ENQ);
            } else if(frame.getDataWithFrame().charAt(0) == AstmFrame.EOT) {
                frame.setType(AstmFrame.FRAME_TYPE_EOT);
            } else if(frame.getDataWithFrame().charAt(0) == AstmFrame.ACK) {
                frame.setType(AstmFrame.FRAME_TYPE_ACK);
            } else if(frame.getDataWithFrame().charAt(0) == AstmFrame.NACK) {
                frame.setType(AstmFrame.FRAME_TYPE_NACK);
            }
        }
    }

    private String readString(StringBuffer data, int[] indexes) {
        int start = indexes[0] - 2; // Convert to 0-based index and remove STX
        if (data.length() >= start + indexes[1] - 1) {
            return data.substring(start, start + indexes[1]).trim();
        } else {
            return "";
        }
    }

    private double readDouble(StringBuffer data, int[] indexes) {
        String stringValue = readString(data, indexes);
        double value = 0;
        try {
            value = Double.valueOf(stringValue);
        } catch (Exception e) {
            Globals.logException(e);
        }

        return value;
    }

    protected boolean treatResultFrame(AlegriaFrame frame) {
        boolean error = false;
        StringBuffer data = frame.getData();

        String programCode = readString(data, PROGRAM_CODE);
        String sampleNumber = readString(data, SAMPLE_NUMBER);
        String patientId = readString(data, PATIENT_ID);
        String patientName = readString(data, PATIENT_NAME);
        String patientDob = readString(data, PATIENT_DOB);
        String patientSex = readString(data, PATIENT_SEX);

        String sampleDate = readString(data, SAMPLE_DATE);
        double concentration = readDouble(data, CONCENTRATION);
        String concentrationUnit = readString(data, CONCENTRATION_UNIT);
        double fraction1Percent = readDouble(data, FRACTION_1_PERCENT);
        String fraction1Name = readString(data, FRACTION_1_NAME);

        String[] parts = patientName.split(" ");
        String firstName = patientName;
        String lastName = patientName;
        String middleName = "";
        if (parts.length > 0) {
            firstName = parts[0];
            lastName = parts[parts.length - 1];
        }
        if (parts.length > 2) {
            middleName = parts[1];
        }

        L3Message message = new L3Message();

        FocLabSample sample = new FocLabSample(patientId);//Since the sample number is very small, we can use the patient ID as a unique identifier
        sample.setFirstName(firstName);
        sample.setLastName(lastName);
        sample.setMiddleInitial(middleName);
        message.addSample(sample);

        //sample.setDateAndTime();
        FocLabTest test = sample.addTest();

        String lisTestCode = driver.testMaps_getLisCode(fraction1Name);
        if (lisTestCode == null) {
            lisTestCode = fraction1Name;
        }
        test.setResultOk(true);
        test.setValueNotes("");
        test.setNotificationMessage("");
        test.setLabel(lisTestCode);
        test.setValue(fraction1Percent); // Example value
        test.setUnitLabel("%");
        message.addSample(sample);
        driver.notifyListeners(message);

        message.dispose();

        return error;
    }

    public void respondToInquiryIfNecessary(AstmFrame frame) {
        boolean error = false;

        final FocInstrument instrument = driver.getInstrument();
        StringBuffer data = frame.getData();
        String sampleId = readString(data, INQUIRY__PATIENT_ID);

        Globals.logString("Calling sendASampleAnsweringInquiry : "+sampleId);
        if (instrument != null && !driver.reserve()) {
//            try {
//                AlegriaFrameCreator creator = new AlegriaFrameCreator();
//                driver.getL3SerialPort().send(creator.buildAckFrame());
//
//                //Loading the L3Message to send
//                L3SampleTestJoinFilter filter = instrument.getSampleListToSendAfterEnquiry(sampleId);
//                filter.setActive(true);
//                instrument.logString("Loading FocLabSample to Send : " + sampleId);
//                messageSentAsOrderToInstrument = filter.convertToMessage();
//
//                String orderFrame = creator.buildOrderFrame(instrument, messageSentAsOrderToInstrument);
//                driver.getL3SerialPort().send(orderFrame);
//
//                driver.release();
//            } catch (Exception e) {
//                messageSentAsOrderToInstrument = null;
//                Globals.logString("Exception while answering inquiry");
//                Globals.logException(e);
//            }
        }

    }

    protected void markMessageSentToInstrument(L3Message messageReadyToSend) {
        final FocInstrument instrument = driver.getInstrument();
        Iterator iter = messageReadyToSend != null ? messageReadyToSend.sampleIterator() : null;
        while (iter != null && iter.hasNext()) {
            FocLabSample sample = (FocLabSample) iter.next();

            sample.getTestList().iterate(new FocListIterator() {
                @Override
                public boolean treatElement(FocListElement element, FocObject focObj) {
                    FocLabTest test = (FocLabTest) focObj;
                    if (test.getDispatchInstrument().getReferenceInt() == instrument.getReference().getInteger()) {
                        try {
                            test.updateStatus(FocLabTest.TEST_STATUS_ANALYSING);
                        } catch (Exception e) {
                            Globals.logException(e);
                        }
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void received(L3Frame l3Frame) {
        AlegriaFrame frame = (AlegriaFrame) l3Frame;

        extractDataFromFrame(frame);
        if (frame.getType() == AlegriaFrame.FRAME_TYPE_RESULT) {
            boolean error = treatResultFrame((AlegriaFrame) frame);
            String answer = AlegriaFrame.ACK_FRAME;
            if (error) {
                Globals.logString("Prepare NACK answer");
                answer = AlegriaFrame.NACK_FRAME;
            }

            try {
                driver.send(answer);
            } catch (Exception e) {
                Globals.logString("Exception while Sending answer");
                Globals.logException(e);
            }
        } else if (frame.getType() == AlegriaFrame.FRAME_TYPE_INFORMATION_INQUIRY) {
            Globals.logString("Received Information Inquiry Frame");
            respondToInquiryIfNecessary(frame);
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_ACK) {
            if (messageSentAsOrderToInstrument != null) {
                markMessageSentToInstrument(messageSentAsOrderToInstrument);
                dispose_MessageSentAsOrderToInstrument();
            }
            Globals.logString("Received ACK Frame");
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_NACK) {
            dispose_MessageSentAsOrderToInstrument();
            Globals.logString("Received NACK Frame");
        } else if (frame.getType() == AstmFrame.FRAME_TYPE_ENQ) {
            Globals.logString("Received ENQ Frame");
        } else {
            Globals.logString("Received Unknown Frame Type: " + frame.getType());
        }

    }

}
