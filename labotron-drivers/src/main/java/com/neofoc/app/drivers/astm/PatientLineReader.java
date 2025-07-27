package com.neofoc.app.drivers.astm;

public class PatientLineReader extends FrameReader {

    private String patientId = null;
    private String lastName = null;
    private String firstName = null;
    private String midInitial = null;

    private static final int POS_PATIENT_SEQUENCE = 1;
    private static final int POS_PATIENT_ID = 3;
    private static final int POS_PATIENT_NAME = 5;

    private static final int POS_PATIENT_LAST_NAME = 0;
    private static final int POS_PATIENT_FIRST_NAME = 1;
    private static final int POS_PATIENT_MIDDLE_NAME = 2;

    public PatientLineReader() {
        super('|', '^');
    }

    public void readToken(String token, int fieldPos, int compPos) {
        com.foc.Globals.logDetail(" fieldPos:" + fieldPos + " compPos:" + compPos + " token:" + token);

        if (fieldPos == POS_PATIENT_NAME) {
            switch (compPos) {
                case POS_PATIENT_ID:
                    patientId = new String(token);
                    break;
                case POS_PATIENT_FIRST_NAME:
                    firstName = new String(token);
                    break;
                case POS_PATIENT_LAST_NAME:
                    lastName = new String(token);
                    break;
                case POS_PATIENT_MIDDLE_NAME:
                    midInitial = new String(token);
                    break;
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
