package com.neofoc.app.drivers.octa;

import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class OctaFrameCreator extends AstmFrameCreator {

    private static final int FRAME_SIZE = 240;

    private SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");

    private String appendSpacesAlignedRight(String str, int length) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.insert(0, " ");
        }
        if (sb.length() > length) {
            sb.setLength(length); // Trim to length if it exceeds
        }
        return sb.toString();
    }

    private String appendSpacesAlignedLeft(String str, int length) {
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() < length) {
            sb.append(" ");
        }
        if (sb.length() > length) {
            sb.setLength(length); // Trim to length if it exceeds
        }
        return sb.toString();
    }

    public int computeAge(Date dateOfBirth) {
        Calendar today = Calendar.getInstance();
        Calendar birthDate = Calendar.getInstance();
        birthDate.setTime(dateOfBirth);

        int age = today.get(Calendar.YEAR) - birthDate.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < birthDate.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    public String buildAckFrame() {
        return String.valueOf(AstmFrame.STX) + String.valueOf(AstmFrame.ACK) + String.valueOf(AstmFrame.ETX);
    }

    public String buildNAckFrame() {
        return String.valueOf(AstmFrame.STX) + String.valueOf(AstmFrame.ACK) + String.valueOf(AstmFrame.ETX);
    }

    public String buildEotFrame() {
        return String.valueOf(AstmFrame.STX) + String.valueOf(AstmFrame.EOT) + String.valueOf(AstmFrame.ETX);
    }

    public String buildOrderFrame(FocInstrument instrument, L3Message messageReadyToSend) {
        int size = messageReadyToSend != null ? messageReadyToSend.getNumberOfSamples() : 0;

        instrument.logString("Instrument : sendASampleAnsweringInquiry 3 size : " + size);

        if (size > 0) {
            instrument.logString("Instrument : sendASampleAnsweringInquiry 4 nbrSamples : " + messageReadyToSend.getNumberOfSamples());
            for (int i = 0; i < messageReadyToSend.getNumberOfSamples(); i++) {
                FocLabSample sample = messageReadyToSend.getSample(i);
                //messageReadyToSend.getTestList().setSample(sample);

                StringBuffer messageToSend = new StringBuffer();
                messageToSend.append(AstmFrame.STX);
                messageToSend.append("@");
                messageToSend.append("0000");
                messageToSend.append(appendSpacesAlignedRight(sample.getId().trim(), 15));
                String patientName = sample.getMiddleInitial() != null && sample.getMiddleInitial().length() > 0 ? sample.getFirstName().trim() + " " + sample.getMiddleInitial().trim() + " " + sample.getLastName().trim() : sample.getFirstName().trim() + " " +  sample.getLastName().trim();
                messageToSend.append(appendSpacesAlignedRight(patientName, 30));
                messageToSend.append(sdf.format(sample.getDateOfBirth()));
                messageToSend.append(appendSpacesAlignedRight(sample.getSexe(), 1));

                //Age
                int age = computeAge(sample.getDateOfBirth());
                String ageStr = String.format("%03d", age); // e.g., "007"
                messageToSend.append(ageStr);

                //Department
                messageToSend.append(appendSpacesAlignedLeft("HSGLABS", 20));
                messageToSend.append(sdf.format(sample.getDateAndTime()));
                //Concentration
                messageToSend.append("078.5");
                messageToSend.append("FREE FIELD 1                  "); // 30 spaces
                messageToSend.append("FREE FIELD 2                  "); // 30 spaces
                messageToSend.append("FREE FIELD 3                  "); // 30 spaces
                messageToSend.append("FREE FIELD 4                  "); // 30 spaces
                messageToSend.append("FREE FIELD 5                  "); // 30 spaces
                messageToSend.append(AstmFrame.ETX);

                // Read only one record and suppose we are doing the Hb1Ac test
                return messageToSend.toString();
            }
        } else {
            return buildEotFrame();
        }
        return null;
    }

}
