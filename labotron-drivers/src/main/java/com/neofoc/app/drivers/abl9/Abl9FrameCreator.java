package com.neofoc.app.drivers.abl9;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Frame creator for ABL9 (Radiometer) Blood Gas Analyzer.
 *
 * Produces a single SOH block in ABL9 format:
 *   H|\^&|||{name}^{code}|||||NC2L||1|{yyyyMMddHHmmss}
 *   P|1||{patientId}||{lastName}^{firstName}||{yyyyMMdd}|{sex}|||||{age}|years
 *   O|1||Sample #^{sampleId}|||||||ANONYMOUS|||||Arterial^|||||||||F
 *   L|1|N
 */
public class Abl9FrameCreator extends AstmFrameCreator {

    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final SimpleDateFormat DOB_FMT = new SimpleDateFormat("yyyyMMdd");

    /**
     * H|\^&|||{name}^{code}|||||NC2L||1|{datetime}
     *
     * Field layout:
     *  1:\^&  2:empty  3:empty  4:name^code  5-8:empty  9:NC2L  10:empty  11:1  12:datetime
     */
    @Override
    public AstmFrame newHeaderFrame(FocInstrument instrument, int sequence) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_HEADER);

        // Field 1: delimiter definition
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.REPEAT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.ESCAPE_DELIMITER);

        // Fields 2-3 empty, field 4: sender name^code  (3 pipes)
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(instrument.getName());
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(instrument.getCode());

        // Fields 5-8 empty + field 9 separator = 5 pipes, field 9: NC2L
        for (int i = 0; i < 5; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data("NC2L");

        // Field 10 empty, field 11: version = 1, field 12: datetime
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("1");
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(DATETIME_FMT.format(LocalDateTime.now()));

        return frame;
    }

    /**
     * P|{seq}||{patientId}||{lastName}^{firstName}||{DOB}|{sex}|||||{age}|years
     *
     * Field layout:
     *  1:seq  2:empty  3:patientId  4:empty  5:lastName^firstName
     *  6:empty  7:DOB  8:sex  9-12:empty  13:age  14:years
     */
    @Override
    public AstmFrame newPatientFrame(FocInstrument instrument, int sequence, int sequence_num,
                                     String sampleId, String patientId, String firstName,
                                     String lastName, String middleName, Date dob, int age, String sex) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_PATIENT);

        // Field 1: sequence
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));

        // Field 2 empty, field 3: patientId  (2 pipes)
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(patientId != null ? patientId : "");

        // Field 4 empty, field 5: lastName^firstName  (2 pipes)
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(lastName != null ? lastName : "");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(firstName != null ? firstName : "");

        // Field 6 empty, field 7: DOB  (2 pipes)
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        if (dob != null && dob.getTime() > Globals.DAY_TIME) {
            frame.append2Data(DOB_FMT.format(dob));
        }

        // Field 8: sex
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        if (sex != null) {
            frame.append2Data(sex);
        }

        // Fields 9-12 empty + field 13 separator = 5 pipes, field 13: age, field 14: years
        for (int i = 0; i < 5; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        if (age > 0) {
            frame.append2Data(String.valueOf(age));
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
            frame.append2Data("years");
        }

        return frame;
    }

    /**
     * O|{seq}||Sample #^{sampleId}|||||||ANONYMOUS|||||Arterial^|||||||||F
     *
     * Field layout:
     *  1:seq  2:empty  3:Sample #^sampleId  4-9:empty  10:ANONYMOUS
     *  11-14:empty  15:Arterial^  16-23:empty  24:F
     */
    @Override
    public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num,
                                   String specimen, String testId, ArrayList<String> testArrayList,
                                   LocalDateTime collectionDate, String priority,
                                   String rackNumber, String tubePosition) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

        // Field 1: sequence
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));

        // Field 2 empty, field 3: Sample #^sampleId  (2 pipes)
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("Sample #");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(specimen != null ? specimen : "");

        // Fields 4-9 empty (6 fields) + field 10 separator = 7 pipes, field 10: ANONYMOUS
        for (int i = 0; i < 7; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data("ANONYMOUS");

        // Fields 11-14 empty (4 fields) + field 15 separator = 5 pipes, field 15: Arterial^
        for (int i = 0; i < 5; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data("Arterial");
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);

        // Fields 16-23 empty (8 fields) + field 24 separator = 9 pipes, field 24: F
        for (int i = 0; i < 9; i++) {
            frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        }
        frame.append2Data("F");

        return frame;
    }

    /**
     * L|{seq}|N
     *
     * Base generates L|{seq} without the termination code — ABL9 requires |N.
     */
    @Override
    public AstmFrame newLastFrame(FocInstrument instrument, int sequence, int sequence_num) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_LAST);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("N");
        return frame;
    }

    /**
     * ABL9 does not use test codes in the order frame — it runs a fixed blood gas panel.
     * One O frame per sample carrying only the specimen ID is sufficient.
     */
    @Override
    public void scanSampleTestsAndBuildFrames(AstmDriver driver, FocLabSample sam, boolean fromDriver) throws Exception {
        AstmFrame frame = newOrderFrame(driver.getInstrument(), getNextSequence(), 1,
                sam.getSampleId(), null, null, sam.getEntryDateTime(), null, null, null);
        driver.addFrame(frame);
    }
}
