package com.neofoc.app.drivers.astm;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PatientLineReader extends FrameReader {

    private String patientId = null;
    private String lastName = null;
    private String firstName = null;
    private String midInitial = null;

    private int POS_PATIENT_SEQUENCE = 1;
    private int POS_PATIENT_ID = 3;
    private int POS_PATIENT_NAME = 5;

    private int POS_PATIENT_LAST_NAME = 0;
    private int POS_PATIENT_FIRST_NAME = 1;
    private int POS_PATIENT_MIDDLE_NAME = 2;

    public PatientLineReader() {
        super('|', '^');
    }

    public void readToken(String token, int fieldPos, int compPos) {
        com.foc.Globals.logDetail(" fieldPos:" + fieldPos + " compPos:" + compPos + " token:" + token);

        if (fieldPos == POS_PATIENT_NAME) {
            if (compPos == POS_PATIENT_ID) {
                patientId = new String(token);
            }else if (compPos == POS_PATIENT_FIRST_NAME) {
                firstName = new String(token);
            }else if (compPos == POS_PATIENT_LAST_NAME) {
                lastName = new String(token);
            }else if (compPos == POS_PATIENT_MIDDLE_NAME) {
                midInitial = new String(token);
            }
        }
    }

    public String getFirstName() {
        return firstName != null ? firstName : "";
    }

    public String getLastName() {
        return lastName != null ? lastName : "";
    }

    public String getMidInitial() {
        return midInitial != null ? midInitial : "";
    }
}
