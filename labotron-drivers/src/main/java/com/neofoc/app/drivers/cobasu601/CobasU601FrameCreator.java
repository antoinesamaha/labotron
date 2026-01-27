package com.neofoc.app.drivers.cobasu601;

import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

import java.util.Iterator;

public class CobasU601FrameCreator extends AstmFrameCreator {

    public CobasU601FrameCreator() {
    }

    public AstmFrame newHeaderFrame(FocInstrument instrument, int sequence) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_HEADER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        frame.append2Data(AstmFrame.REPEAT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.ESCAPE_DELIMITER);

        for (int i = 0; i < 3; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data("ASTM-Host");

        return frame;
    }

    public AstmFrame newPatientFrame(FocInstrument instrument, int sequence, int sequence_num, String patientId, String firstName, String lastName, String middleName) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_PATIENT);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        return frame;
    }

    public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num, String specimen, String testId) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(specimen, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        //DIFF frame.append2Data(specimen, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        for (int i = 0; i < 3; i++) {
            frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        }
        String instrCode = ((DriverSerialPort) instrument.getDriver()).testMaps_getInstCode(testId);
        frame.append2Data(instrCode, getTestCodeLength(instrument));
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data('0');

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data('R');

        for (int i = 0; i < 6; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data('N');
        for (int i = 0; i < 14; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data('O');
        return frame;
    }

    public AstmFrame newCommentFrame(FocInstrument instrument, int sequence, int sequence_num, FocLabSample sam) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_COMMENT);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("I");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sam.getFirstName() + " " + sam.getLastName());
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("G");
        return frame;
    }

    public AstmFrame newLastFrame(FocInstrument instrument, int sequence, int sequence_num) {
        AstmFrame frame = super.newLastFrame(instrument, sequence, sequence_num);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data('F');
        return frame;
    }

    public void scanSampleTestsAndBuildFrames(AstmDriver driver, FocLabSample sam, boolean fromDriver) throws Exception {
        AstmFrame frame = new AstmFrame(driver.getInstrument(), getNextSequence(), AstmFrame.FRAME_TYPE_ORDER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(1));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sam.getSampleId(), 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        //DIFF frame.append2Data(specimen, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        boolean firstTest = true;
        Iterator tIter = sam.testIterator();
        while (tIter != null && tIter.hasNext()) {
            FocLabTest test = (FocLabTest) tIter.next();
            if (test != null) {
                if (!firstTest) {
                    frame.append2Data(AstmFrame.REPEAT_DELIMITER);
                }
                for (int i = 0; i < 3; i++) {
                    frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                }
                String instrCode = ((DriverSerialPort) driver).testMaps_getInstCode(test.getLabel());
                frame.append2Data(instrCode, getTestCodeLength(driver.getInstrument()));
                //frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                //frame.append2Data('0');
            }
            firstTest = false;
        }

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data('R');

        for (int i = 0; i < 6; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data('N');
        for (int i = 0; i < 14; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data('O');
        driver.addFrame(frame);
    }
}
