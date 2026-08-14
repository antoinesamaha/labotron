package com.neofoc.app.drivers.abl9;

import com.neofoc.app.drivers.astm.PatientLineReader;

public class Abl9PatientLineReader extends PatientLineReader {

    // P|1||201910236||Ahmad[.]Baydoun^Khadijeh||19380804|F|||||87|years|

    private String patientId = null;
    private String lastName = null;
    private String firstName = null;
    private String midInitial = null;

    private int POS_PATIENT_ID = 3;
    private int POS_PATIENT_NAME = 5;
    //private int POS_PATIENT_GENDER = 8;

    private int CMP_PATIENT_FIRST_NAME = 0;
    private int CMP_PATIENT_LAST_NAME = 1;

    public Abl9PatientLineReader() {
        super();
    }

    public void readToken(String token, int fieldPos, int compPos) {
        com.foc.Globals.logDetail(" fieldPos:" + fieldPos + " compPos:" + compPos + " token:" + token);

        if (fieldPos == POS_PATIENT_ID) {
            patientId = new String(token);
        } else if (fieldPos == POS_PATIENT_NAME) {
            if (compPos == CMP_PATIENT_FIRST_NAME) {
                firstName = new String(token);
                if (firstName.contains(" ")) {
                    midInitial = firstName.substring(firstName.lastIndexOf(" ") + 1);
                    firstName = firstName.substring(0, firstName.lastIndexOf(" "));
                }
            }else if (compPos == CMP_PATIENT_LAST_NAME) {
                lastName = new String(token);
            }
        }
    }

    public String getPatientId() {
        return patientId != null ? patientId : "";
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
