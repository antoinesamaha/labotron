package com.neofoc.app.drivers.gempremier3500;

import com.foc.Globals;
import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;

public class Gem3500FrameCreator extends AstmFrameCreator {

    /*
        Documentation frame example:

        H|\^&|||Harvard Hospital Host|||||||||20021023120023<CR
        P|1||112233998877||BLAKE^LINDSEY||19740722|F<CR>
        O|1|33745677|||||||||||||A<CR>
        L|1<CR>
     */

    private String lastSampleId = null;

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

        //  P|1||112233998877||BLAKE^LINDSEY||19740722|F<CR>
        lastSampleId = sampleId;

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(firstName);
        frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
        frame.append2Data(lastName);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        if (dob != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            frame.append2Data(sdf.format(dob));
        }
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sex);
        return frame;
    }

    public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num, String specimen, String testId, ArrayList<String> testArrayList, LocalDateTime collectionDate, String priority, String rackNumber, String tubePosition) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

        //        O|1|SAMPLE01||^^^SS-B
        //        O|1|SAMPLE01||^^^SS-B|R||||||N||||||||||||||QC
        // O|1|33745677|||||||||||||A<CR>

        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequence_num));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(lastSampleId);

        frame.append2Data(AstmFrame.FIELD_SEPERATOR, 13);
        frame.append2Data("A");

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
