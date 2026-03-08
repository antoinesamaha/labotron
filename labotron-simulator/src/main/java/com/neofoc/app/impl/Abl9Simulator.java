package com.neofoc.app.impl;

import com.neofoc.app.Phase;
import com.neofoc.app.SimSocket;

/**
 * Simulator for ABL9 (Radiometer) Blood Gas Analyzer.
 *
 * Simulates bidirectional ASTM inquiry-based communication:
 *
 * 1. Sends Q record to query Labotron for patient/order info
 *    Q|1|PID{patientId}^ACN{accessionNbr}|||||||||D
 *
 * 2. Receives H/P/O/L response from Labotron (patient demographics + order)
 *
 * 3. Simulates measurement delay, then sends blood gas results
 *
 * ABL9 test codes used: pH, pCO2, pO2, HCO3-, ABE, K+, Na+, Ca++, Cl-,
 *   Glu, Lac, tHb, sO2, Hct
 */
public class Abl9Simulator extends AbstractSimulator {

    /**
     * Inquiry frames — ABL9 queries Labotron for patient info by accession number.
     * Format: Q|1|PID{patientId}^ACN{accessionNbr}|||||||||D
     * "D" = query by patient ID; "O" = query by accession number
     */
    String[] INQUIRY_FRAMES = {
        "1H|\\^&|||ABL9^Radiometer||||||P||20260210090000",
        "2Q|1|PID123456789^ACN5647687|||||||||D",
        "3L|1|N"
    };

    /**
     * Blood gas result frames — normal patient values.
     * Test code format: ^^^{testCode}^M  (M = measured)
     * Based on ABL9 ASTM/HL7 parameter names from documentation.
     */
    String[] RESULT_FRAMES = {
        // Header
        "1H|\\^&|||ABL9^Radiometer||||||P||20260210090500",

        // Patient
        "2P|1||123456789||Doe^John^M||19850315|M",

        // Order - sample 5647687, arterial blood
        "3O|1||5647687|^^^WB|||20260210090000||||||||Arterial^Brachial, left|Dr. McCoy||||||||F|",

        // Blood gas results - pH/pCO2/pO2
        "4R|1|^^^pH^M|7.402|||N||F||admin|20260210090100|ABL9",
        "5R|2|^^^pCO2^M|42|mmHg||N||F||admin|20260210090100|ABL9",
        "6R|3|^^^pO2^M|95|mmHg||N||F||admin|20260210090100|ABL9",

        // Acid-base
        "7R|4|^^^HCO3-^M|24.5|mmol/L||N||F||admin|20260210090100|ABL9",
        "0R|5|^^^ABE^M|0.5|mmol/L||N||F||admin|20260210090100|ABL9",

        // Electrolytes
        "1R|6|^^^K+^M|4.2|mmol/L||N||F||admin|20260210090100|ABL9",
        "2R|7|^^^Na+^M|142|mmol/L||N||F||admin|20260210090100|ABL9",
        "3R|8|^^^Ca++^M|1.25|mmol/L||N||F||admin|20260210090100|ABL9",
        "4R|9|^^^Cl-^M|99|mmol/L||N||F||admin|20260210090100|ABL9",

        // Metabolites
        "5R|10|^^^Glu^M|5.0|mmol/L||N||F||admin|20260210090100|ABL9",
        "6R|11|^^^Lac^M|1.2|mmol/L||N||F||admin|20260210090100|ABL9",

        // Co-oximetry
        "7R|12|^^^tHb^M|10.9|g/dL||N||F||admin|20260210090100|ABL9",
        "0R|13|^^^sO2^M|97.5|%||N||F||admin|20260210090100|ABL9",
        "1R|14|^^^Hct^M|42|%||N||F||admin|20260210090100|ABL9",

        // Comment and terminator
        "2C|1|I|Quality Control: OK|G",
        "3L|1|N"
    };

    public void simulate() {
        phase = Phase.OPENING_SOCKET;

        socket = new SimSocket(this, 9002);
        socket.open();

        sendingInquiry();
        receivingOrderResponse();
        sendingResults();
    }

    /**
     * Sends Q record to Labotron querying for patient/order info.
     * Runs in a separate thread; transitions to RECEIVING_SAMPLES_INQUIRY_RESPONSE
     * after the inquiry EOT is sent.
     */
    public void sendingInquiry() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("ABL9: Waiting for socket connection...");
                    while (socket.socket == null || !socket.socket.isConnected()) {
                        sleep(500);
                    }
                    phase = Phase.RECEIVING_SAMPLES;
                    System.out.println("ABL9: Sending inquiry for patient order (ACN5647687)...");
                    sendingFrames(INQUIRY_FRAMES);
                    System.out.println("ABL9: Inquiry sent. Waiting for order response from host...");
                    phase = Phase.RECEIVING_SAMPLES_INQUIRY_RESPONSE;
                } catch (Exception e) {
                    System.out.println("ABL9 inquiry error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Receives the host's response to the inquiry (H/P/O/L frames).
     * ACKs each incoming frame; transitions to SENDING_RESULTS when EOT is received.
     */
    public void receivingOrderResponse() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("ABL9: Order response receiver started.");

                    while (phase.compareTo(Phase.RECEIVING_SAMPLES_INQUIRY_RESPONSE) <= 0) {
                        if (phase == Phase.RECEIVING_SAMPLES_INQUIRY_RESPONSE
                                && socket.socket != null && socket.socket.isConnected()) {

                            String response = socket.receive();

                            if (response != null && !response.isEmpty()) {
                                System.out.println("ABL9 received from host: " + response);

                                if (response.charAt(0) == EOT) {
                                    System.out.println("ABL9: Order response complete (EOT). Simulating measurement (3s)...");
                                    sleep(3000);
                                    phase = Phase.SENDING_RESULTS;
                                } else if (response.charAt(0) == ENQ) {
                                    // Host requesting to send order info
                                    System.out.println("ABL9: Received ENQ from host, sending ACK.");
                                    socket.send("" + ACK);
                                } else {
                                    // Data frame (H, P, O, L) - ACK it
                                    socket.send("" + ACK);
                                }
                            }
                        }
                        sleep(500);
                    }
                } catch (Exception e) {
                    System.out.println("ABL9 order receiver error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends blood gas results once measurement simulation is complete.
     * Waits for SENDING_RESULTS phase before transmitting.
     */
    public void sendingResults() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("ABL9: Result sender waiting for measurement to complete...");
                    while (phase.compareTo(Phase.SENDING_RESULTS) < 0) {
                        sleep(1000);
                    }
                    System.out.println("ABL9: Sending blood gas results...");
                    sendingFrames(RESULT_FRAMES);
                    phase = Phase.DONE;
                    System.out.println("ABL9: Result transmission complete.");
                } catch (Exception e) {
                    System.out.println("ABL9 result sender error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
