package com.neofoc.app.drivers.abl9;

import com.neofoc.app.drivers.astm.InformationInquiryReader;

/**
 * Q record reader for ABL9 (Radiometer) analyzer.
 *
 * ABL9 Q record format (ASTM field 3):
 *   PID{patientId}^ACN{accessionNumber}
 *
 * Example:
 *   Q|1|PID1234862255^ACN2233445609|||||||||D
 *
 * Component 0: PID{patientId}   - patient ID with "PID" prefix
 * Component 1: ACN{accessionId} - accession number with "ACN" prefix (used as sampleId)
 */
public class Abl9InformationInquiryReader extends InformationInquiryReader {
    /*
    //[SOH]
    String[] RESULT_FRAMES = {
    "H|\\^&|||ABL9^402843|||||NC2L||1|20260627193917",
    "P|1||201910236||Ahmad Baydoun^Khadijeh||19380804|F|||||87|years",
    "O|1||Sample #^28258|||||||ANONYMOUS|||||Arterial^|||||||||F",
    "L|1|N"
    };
    //[EOT]
    */

    private static final int FLD_STARTING_RANGE = 2;
    private static final int CMP_PATIENT_ID     = 0;
    private static final int CMP_ACCESSION_NBR  = 1;

    private static final String PID_PREFIX = "PID";
    private static final String ACN_PREFIX = "ACN";

    @Override
    public void readToken(String token, int fieldPos, int compPos) {
        if (fieldPos == FLD_STARTING_RANGE) {
            if (compPos == CMP_PATIENT_ID && token.startsWith(PID_PREFIX)) {
                // Patient ID - strip "PID" prefix (not used for sample lookup)
                setSampleId(token.substring(PID_PREFIX.length()));
            } else if (compPos == CMP_ACCESSION_NBR && token.startsWith(ACN_PREFIX)) {
                // Accession number - strip "ACN" prefix, use as sampleId for LIS lookup
                //setSampleId(token.substring(ACN_PREFIX.length()));
            }
        }
    }
}
