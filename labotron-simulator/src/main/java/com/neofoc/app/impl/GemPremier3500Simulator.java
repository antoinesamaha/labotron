package com.neofoc.app.impl;

import com.neofoc.app.Phase;
import com.neofoc.app.SimSocket;

/**
 * Simulator for GEM Premier 3500 Blood Gas Analyzer
 *
 * Simulates bidirectional ASTM communication:
 * - Receives sample orders from host (Labotron)
 * - Sends back blood gas test results
 *
 * Based on ASTM E1394-91 and E1381-91 protocols
 */
public class GemPremier3500Simulator extends AbstractSimulator {

    /**
     * GEM Premier 3500 test result frames
     * Frame examples based on GEM Premier 3500 Interface Protocol documentation
     *
     * Blood Gas Parameters:
     * - pH (pH blood gas)
     * - pCO2 (Partial pressure CO2)
     * - pO2 (Partial pressure O2)
     * - HCO3 (Bicarbonate)
     * - BE (Base Excess)
     * - SO2 (Oxygen Saturation)
     * - Na (Sodium)
     * - K (Potassium)
     * - Ca (Calcium)
     * - Glu (Glucose)
     * - Lac (Lactate)
     * - Hct (Hematocrit)
     */
    String[] GEM_PREMIER_3500_RESULT_FRAMES = {
        // Header - Sender: GEM Premier 3500
        "1H|\\^&|||GEM Premier 3500^IL^1.0||||||TSDWN^BATCH|P|1|20260210120000",

        // Patient Record
        "2P|1|||12345||Doe^John^M||19850315|M",

        // Order Record - echoes the sample ID
        "3O|1|SAMPLE001||^^^pH^\\^^^pCO2^\\^^^pO2^\\^^^HCO3^\\^^^BE^|R||||||N||||WB|||||||O",

        // Result Records - Blood Gas panel results
        // pH - normal range 7.35-7.45
        "4R|1|^^^pH||7.38||7.35^7.45|N||F||admin|20260210120100|GEM3500",

        // pCO2 - Partial pressure CO2, normal 35-45 mmHg
        "5R|2|^^^pCO2||42|mmHg|35^45|N||F||admin|20260210120100|GEM3500",

        // pO2 - Partial pressure O2, normal 80-100 mmHg
        "6R|3|^^^pO2||95|mmHg|80^100|N||F||admin|20260210120100|GEM3500",

        // HCO3 - Bicarbonate, normal 22-26 mmol/L
        "7R|4|^^^HCO3||24.5|mmol/L|22^26|N||F||admin|20260210120100|GEM3500",

        // BE - Base Excess, normal -2 to +2 mmol/L
        "1R|5|^^^BE||0.5|mmol/L|-2^2|N||F||admin|20260210120100|GEM3500",

        // SO2 - Oxygen Saturation, normal 95-100%
        "2R|6|^^^SO2||98.2|%|95^100|N||F||admin|20260210120100|GEM3500",

        // Na - Sodium, normal 136-145 mmol/L
        "3R|7|^^^Na||142|mmol/L|136^145|N||F||admin|20260210120100|GEM3500",

        // K - Potassium, normal 3.5-5.0 mmol/L
        "4R|8|^^^K||4.2|mmol/L|3.5^5.0|N||F||admin|20260210120100|GEM3500",

        // Ca - Calcium, normal 1.12-1.32 mmol/L
        "5R|9|^^^Ca||1.25|mmol/L|1.12^1.32|N||F||admin|20260210120100|GEM3500",

        // Glu - Glucose, normal 70-110 mg/dL
        "6R|10|^^^Glu||95|mg/dL|70^110|N||F||admin|20260210120100|GEM3500",

        // Lac - Lactate, normal 0.5-2.2 mmol/L
        "7R|11|^^^Lac||1.5|mmol/L|0.5^2.2|N||F||admin|20260210120100|GEM3500",

        // Hct - Hematocrit, normal 36-46%
        "0R|12|^^^Hct||42|%|36^46|N||F||admin|20260210120100|GEM3500",

        // Comment Record
        "1C|1|I|Quality Control: OK|G",

        // Terminator Record
        "2L|1|N"
    };

    /**
     * Alternative result set with abnormal values for testing
     */
    String[] GEM_PREMIER_3500_ABNORMAL_RESULTS = {
        "1H|\\^&|||GEM Premier 3500^IL^1.0||||||TSDWN^BATCH|P|1|20260210120500",
        "2P|1|||67890||Smith^Jane^A||19920610|F",
        "3O|1|SAMPLE002||^^^pH^\\^^^pCO2^\\^^^pO2^|R||||||N||||WB|||||||O",

        // Low pH - Acidosis
        "4R|1|^^^pH||7.28||7.35^7.45|L||F||admin|20260210120500|GEM3500",

        // High pCO2 - Respiratory acidosis
        "5R|2|^^^pCO2||58|mmHg|35^45|H||F||admin|20260210120500|GEM3500",

        // Low pO2 - Hypoxemia
        "6R|3|^^^pO2||68|mmHg|80^100|L||F||admin|20260210120500|GEM3500",

        "7C|1|I|Abnormal results - review patient|G",
        "0L|1|N"
    };

    public void simulate() {
        phase = Phase.OPENING_SOCKET;

        socket = new SimSocket(this, 9001);

        // Open socket and start parallel threads
        socket.open();
        receivingSamples();
        sendingResults();

        // Keep simulator running
        // socket.close(); // Uncomment to close after execution
    }

    /**
     * Receives sample orders from Labotron host
     * Responds with ACK to each frame received
     */
    public void receivingSamples() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    System.out.println("GEM Premier 3500: Waiting to receive sample orders...");

                    while (phase.compareTo(Phase.RECEIVING_SAMPLES) <= 0) {
                        if (socket.socket != null && socket.socket.isConnected()) {
                            String response = socket.receive();

                            if (response != null && !response.isEmpty()) {
                                System.out.println("GEM Premier 3500 received: " + response);

                                // Check frame type
                                if (response.charAt(0) == EOT) {
                                    System.out.println("GEM Premier 3500: Received EOT, switching to sending results");
                                    phase = Phase.SENDING_RESULTS;
                                    sleep(2000); // Wait 2 seconds before sending results
                                } else if (response.charAt(0) == ENQ) {
                                    System.out.println("GEM Premier 3500: Received ENQ, sending ACK");
                                    socket.send("" + ACK);
                                } else {
                                    // Data frame received, send ACK
                                    socket.send("" + ACK);
                                }
                            }
                        }

                        // Poll every 2 seconds
                        sleep(2000);
                    }
                } catch (Exception e) {
                    System.out.println("GEM Premier 3500 receiver error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * Sends result frames back to Labotron host
     * Waits for phase to be SENDING_RESULTS before transmitting
     */
    public void sendingResults() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    int frameAt = -1;

                    while (phase.compareTo(Phase.SENDING_RESULTS) <= 0) {
                        if (phase.compareTo(Phase.SENDING_RESULTS) == 0) {

                            if (frameAt == -1) {
                                // Send ENQ to start transmission
                                System.out.println("GEM Premier 3500: Sending ENQ to start result transmission");
                                socket.send("" + ENQ);

                            } else if (frameAt < GEM_PREMIER_3500_RESULT_FRAMES.length) {
                                // Send result frame
                                String frame = GEM_PREMIER_3500_RESULT_FRAMES[frameAt];
                                System.out.println("GEM Premier 3500: Sending frame [" + frameAt + "]: " + frame);
                                StringBuffer sbFrameWithData = createDataWithFrame(frame);
                                socket.send(sbFrameWithData.toString());

                            } else {
                                // Send EOT to end transmission
                                System.out.println("GEM Premier 3500: Sending EOT to end result transmission");
                                socket.send("" + EOT);
                                phase = Phase.DONE;
                            }

                            sleep(1000);

                            // Wait for ACK/NACK
                            char responseChar = readResponseChar();
                            if (responseChar == ACK) {
                                System.out.println("GEM Premier 3500: Received ACK, proceeding to next frame");
                                frameAt++;
                            } else if (responseChar == NACK) {
                                System.out.println("GEM Premier 3500: Received NACK, resending frame");
                                // Don't increment frameAt, resend same frame
                            }

                        } else {
                            // Not ready to send yet, wait
                            sleep(1000);
                        }
                    }

                    System.out.println("GEM Premier 3500: Result transmission complete");

                } catch (Exception e) {
                    System.out.println("GEM Premier 3500 sender error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
