package com.neofoc.app.drivers.alegria;

import com.foc.Globals;
import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.drivers.astm.AstmParams;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class AlegriaFrameCreator extends AstmFrameCreator {

    /*
        Actual frame example:

        H|\^&
        P|1|5586677|797979
        O|1|5586677|^^^SS-B
        O|2|5586677|^^^SS-A
        L|1|N

        Documentation frame example:
        H|\^&
        P|1|SAMPLE01|123123123
        O|1|SAMPLE01||^^^SS-B
        L|1|N

     */

    public AstmFrame newHeaderFrame(FocInstrument instrument, int sequence) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_HEADER);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        frame.append2Data(AstmFrame.REPEAT_DELIMITER);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(AstmFrame.ESCAPE_DELIMITER);

        return frame;
    }

    public AstmFrame newPatientFrame(FocInstrument instrument, int sequence, int sequence_num, String sampleId, String patientId, String firstName, String lastName, String middleName, Date dob, int age, String sex) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_PATIENT);

        //  P|1|SAMPLE01|123123123

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(patientId);

        return frame;
    }

    public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num, String specimen, String testId, ArrayList<String> testArrayList, LocalDateTime collectionDate, String priority, String rackNumber, String tubePosition) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

        //        O|1|SAMPLE01||^^^SS-B

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(specimen);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);

        String instrCode = ((DriverSerialPort) instrument.getDriver()).testMaps_getInstCode(testId);
        for (int d = 0; d < 3; d++) {
            frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        }
        frame.append2Data(instrCode);

        return frame;
    }

    public AstmFrame newLastFrame(FocInstrument instrument, int sequence, int sequence_num) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_LAST);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data("N");
        return frame;
    }
}
