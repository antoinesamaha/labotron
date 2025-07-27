package com.neofoc.app.drivers.astm;

import com.foc.Globals;
import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

public class AstmFrameCreator {
    protected int frameSequence = 0;

    public AstmFrameCreator() {
    }

    public void dispose() {
    }

    protected void resetSequence() {
        frameSequence = -1;
    }

    protected int getNextSequence() {
        int s = getNextSequence_WithoutIncrementing();
        incrementSequence();
        return s;
    }

    protected int getNextSequence_WithoutIncrementing() {
        int s = frameSequence + 1;
        if (s == 8) s = 0;
        return s;
    }

    protected void incrementSequence() {
        frameSequence = getNextSequence_WithoutIncrementing();
    }

    public AstmFrame newEnquiryFrame(FocInstrument instrument) {
        AstmFrame frame = new AstmFrame(instrument, AstmFrame.SEQUENCE_IRRELEVANT, AstmFrame.FRAME_TYPE_ENQ);
        return frame;
    }

    public AstmFrame newHeaderFrame(FocInstrument instrument, int sequence) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_HEADER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        frame.append2Data(AstmFrame.REPEAT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.ESCAPE_DELIMITER);

        for (int i = 0; i < 10; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }

        frame.append2Data('P');
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data('1');

        return frame;
    }

    public AstmFrame newPatientFrame(FocInstrument instrument, int sequence, int sequence_num, String sampleId, String firstName, String lastName, String middleName) {
        return newPatientFrame(instrument, sequence, sequence_num, sampleId, "", firstName, lastName, middleName, null, 0, null);
    }

    public AstmFrame newPatientFrame(FocInstrument instrument, int sequence, int sequence_num, String sampleId, String patientId, String firstName, String lastName, String middleName, Date dob, int age, String sex) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_PATIENT);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        //frame.append2Data(patientId, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        String patientId_Local = "";
        try {
            AstmDriver driver = (AstmDriver) instrument.getDriver();
            if (driver != null && driver.getAstmParams() != null) {
                if (driver.getAstmParams().isSendPatientIdToInstrument()) {
                    patientId_Local = patientId;
                }
            }
        } catch (Exception e) {
        }
        frame.append2Data(patientId_Local, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(lastName, 15);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(firstName, 20);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(middleName, 1);

        try {
            AstmParams astmParams = ((AstmDriver) instrument.getDriver()).getAstmParams();
            if (age > 0 && astmParams.isSendPatientAgeAndSex()) {
                frame.append2Data(AstmFrame.FIELD_SEPERATOR);
                //Mother Maiden name
                frame.append2Data(AstmFrame.FIELD_SEPERATOR);

                //The DOB and AGE fields
                if (astmParams.isSendPatientDateOfBirth()) {
                    if (dob != null && dob.getTime() > Globals.DAY_TIME) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                        frame.append2Data(sdf.format(dob));
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                    } else {
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                    }
                } else {
                    //In this case send the age only
                    if (age > 0) {
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                        frame.append2Data("" + age);
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                        frame.append2Data("Y");
                    } else {
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
                    }
                }

                frame.append2Data(AstmFrame.FIELD_SEPERATOR);

                //Sex Field
                if (sex != null) {
                    frame.append2Data(sex);
                }
            }
        } catch (Exception e) {
            frame.logException(e);
        }

        return frame;
    }

    protected int getTestCodeLength(FocInstrument instr) throws Exception {
        int length = 0;
        AstmDriver driver = (AstmDriver) instr.getDriver();
        if (driver != null) {
            length = driver.getAstmParams().getTestCodeLength();
        } else {
            length = 3;
        }
        return length;
    }

    protected void appendASingleTest(AstmFrame frame, String instrCode, int testCodeLength) throws Exception {
        for (int i = 0; i < 3; i++) {
            frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        }
        frame.append2Data(instrCode, testCodeLength);
    }

    public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num, String specimen, String testId, Date collectionDate) throws Exception {
        return newOrderFrame(instrument, sequence, sequence_num, specimen, testId, null, collectionDate, null, null, null);
    }

    public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num, String specimen, String testId, ArrayList<String> testArrayList, Date collectionDate, String priority, String rackNumber, String tubePosition) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(specimen, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(specimen, 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        if (testArrayList != null) {
            for (int i = 0; i < testArrayList.size(); i++) {
                if (i > 0) frame.append2Data(AstmFrame.REPEAT_DELIMITER);
                appendASingleTest(frame, testArrayList.get(i), getTestCodeLength(instrument));
            }

        } else {
            String instrCode = ((DriverSerialPort) instrument.getDriver()).testMaps_getInstCode(testId);
            appendASingleTest(frame, instrCode, getTestCodeLength(instrument));
//    	for (int i=0; i<3; i++){
//    		frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
//    	}
//    	String instrCode = ((DriverSerialPort)instrument.getDriver()).testMaps_getInstCode(testId);
//    	frame.append2Data(instrCode, getTestCodeLength(instrument));    	
        }

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        if (priority != null) frame.append2Data(priority, 1);
        for (int i = 0; i < 2; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if (collectionDate.getTime() < 24 * 60 * 60 * 1000) {
            frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
        } else {
            frame.append2Data(sdf.format(collectionDate));
        }

        for (int i = 0; i < 4; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data('N');
        for (int i = 0; i < 14; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data('O');
        return frame;
    }

    public AstmFrame newResultFrame(FocInstrument instrument, int sequence, int sequence_num, String testId, String resultUnit, double resultValue) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_RESULT);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        for (int i = 0; i < 3; i++) {
            frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        }
        String instrCode = ((DriverSerialPort) instrument.getDriver()).testMaps_getInstCode(testId);
        frame.append2Data(instrCode, getTestCodeLength(instrument));
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data('F');
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(resultValue), 15);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(resultUnit, 7);
        return frame;
    }

    public AstmFrame newLastFrame(FocInstrument instrument, int sequence, int sequence_num) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_LAST);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        return frame;
    }

    public AstmFrame newEndOfTransmissionFrame(FocInstrument instrument) {
        AstmFrame frame = new AstmFrame(instrument, AstmFrame.SEQUENCE_IRRELEVANT, AstmFrame.FRAME_TYPE_EOT);
        return frame;
    }

    public String getProfileForTestID(String testLabel) {
        return null;
    }

    public void scanSampleTestsAndBuildFrames(AstmDriver driver, FocLabSample sam, boolean fromDriver) throws Exception {
        int testSequence = 1;

        boolean useProfiles = driver.getAstmParams().isSendProfileInsteadOfTestID() && fromDriver;
        if (useProfiles) {
            ArrayList<String> profilesArray = new ArrayList<String>();
            boolean urgent = false;
            Iterator tIter = sam.testIterator();
            while (tIter != null && tIter.hasNext()) {
                FocLabTest test = (FocLabTest) tIter.next();
                if (test != null) {
                    urgent = urgent || test.getPriority().equals("S");
                    String instrCode = driver.testMaps_getInstCode(test.getLabel());
                    String profile = getProfileForTestID(instrCode);
                    if (!profilesArray.contains(profile)) {
                        profilesArray.add(profile);
                    }
                }
            }

            AstmFrame frame = newOrderFrame(driver.getInstrument(), getNextSequence(), testSequence, sam.getId(), null, profilesArray, sam.getEntryDate(), urgent ? "S" : "R", null, null);
            driver.addFrame(frame);
        } else {
            Iterator tIter = sam.testIterator();
            while (tIter != null && tIter.hasNext()) {
                FocLabTest test = (FocLabTest) tIter.next();
                if (test != null) {
                    // Order Frame
                    AstmFrame frame = newOrderFrame(driver.getInstrument(), getNextSequence(), testSequence, sam.getId(), test.getLabel(), null, sam.getEntryDate(), test.getPriority(), null, null);
                    driver.addFrame(frame);
                    // ---------------
                    if (fromDriver == false) {
                        //Result Frame
                        frame = newResultFrame(driver.getInstrument(), getNextSequence(), 1, test.getLabel(), test.getUnitLabel(), test.getValue());
                        driver.addFrame(frame);
                        //----------------
                    }
                }
                testSequence++;
            }
        }
    }

    public AstmFrame newCommentFrame(FocInstrument instrument, int sequence, int sequence_num, FocLabSample sam) throws Exception {
        return null;
    }

    public AstmFrame newSpecificComments(FocInstrument instrument, int sequence, int sequence_num, FocLabSample sam) throws Exception {
        return null;
    }

    public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
        resetSequence();

        // ENQ frame
        // -----------
        getNextSequence();
        AstmFrame frame = newEnquiryFrame(driver.getInstrument());
        driver.addFrame(frame);
        // -----------

        // Header frame
        frame = newHeaderFrame(driver.getInstrument(), getNextSequence());
        driver.addFrame(frame);
        // -------------

        // Patient Frame
        int sampleSequence = 1;

        Iterator sIter = message.sampleIterator();
        while (sIter != null && sIter.hasNext()) {
            FocLabSample sam = (FocLabSample) sIter.next();
            if (sam != null) {

                frame = newPatientFrame(driver.getInstrument(), getNextSequence(), sampleSequence, sam.getId(), sam.getPatientId(), sam.getFirstName(), sam.getLastName(), sam.getMiddleInitial(), sam.getDateOfBirth(), sam.getAge(), sam.getSexe());
                driver.addFrame(frame);

                scanSampleTestsAndBuildFrames(driver, sam, fromDriver);

                frame = newSpecificComments(driver.getInstrument(), getNextSequence_WithoutIncrementing(), sampleSequence, sam);//For conventional ML DISABLED
                if (frame != null) {
                    incrementSequence();
                    driver.addFrame(frame);
                }

                // Comment frame
                if (driver.getAstmParams().isSendCommentFrameFromHost()) {
                    frame = newCommentFrame(driver.getInstrument(), getNextSequence(), sampleSequence, sam);
                    if (frame != null) {
                        driver.addFrame(frame);
                    }
                }
                // -------------

                sampleSequence++;
            }
        }

        // Last frame
        frame = newLastFrame(driver.getInstrument(), getNextSequence(), 1);
        driver.addFrame(frame);
        // -------------

        // EOT frame
        frame = newEndOfTransmissionFrame(driver.getInstrument());
        driver.addFrame(frame);
        // -------------
    }
}
