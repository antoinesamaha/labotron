package com.neofoc.app.impl;

import com.neofoc.app.Phase;
import com.neofoc.app.SimSocket;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class InfinitySimulator extends AbstractSimulator {

    // Loaded from src/main/resources/infinity_result_frames.txt
    // Contains all 45,977 result frames extracted from the real log
    // (2026-02-11 Cobas Infinity full-day session)
    private String[] resultFrames;

    public void simulate() {
        //resultFrames = loadFrames("infinity_result_frames_all.txt");
        resultFrames = loadFrames("infinity_result_frames_small.txt");
        phase = Phase.OPENING_SOCKET;

        socket = new SimSocket(this, 9000);

        //These 3 calls will run in 3 parallel threads
        socket.open();

//        receivingOrders();
//        sleep(10000);
        sendingResults();
        sleep(120000);
//        socket.close();
    }

    public void receivingOrders() {
        socket.setSendingMode(true);
        phase = Phase.RECEIVING_SAMPLES;
        System.out.println("INFINITY: waiting for orders...");
        try {
            while (phase == Phase.RECEIVING_SAMPLES) {
                if (socket.socket != null && socket.socket.isConnected()) {
                    String data = socket.receive();
                    if (data != null && !data.isEmpty()) {
                        char first = data.charAt(0);
                        if (first == EOT) {
                            System.out.println("INFINITY: orders complete, preparing results...");
                            phase = Phase.SENDING_RESULTS;
                        } else {
                            socket.send("" + ACK);
                        }
                    }
                }
                sleep(200);
            }
        } catch (Exception e) {
            System.out.println("INFINITY receivingOrders error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendingResults() {
        sendingFrames(resultFrames, false);
    }

    // ---------------------------------------------------------------------------
    // Entry point
    // ---------------------------------------------------------------------------

//    @Override
//    public void simulate() {
//        resultFrames = loadFrames("infinity_result_frames.txt");
//        System.out.println("INFINITY: loaded " + resultFrames.length + " result frames");
//
//        phase = Phase.OPENING_SOCKET;
//        socket = new SimSocket(this, 9000);
//        socket.open();
//        receivingOrders();
//        sendingResults();
//        while (phase != Phase.DONE) {
//            sleep(1000);
//        }
//        socket.close();
//    }

    // ---------------------------------------------------------------------------
    // Phase 1 – Receive orders from driver
    // Driver sends: ENQ → H → P → O (all tests) → C → EOT
    // We ACK every frame and wait for EOT to transition.
    // ---------------------------------------------------------------------------

//    private void receivingOrders() {
//        new Thread(() -> {
//            // Disable SimSocket auto-ACK thread so this thread owns the receive loop
//            socket.setSendingMode(true);
//            phase = Phase.RECEIVING_SAMPLES;
//            System.out.println("INFINITY: waiting for orders...");
//            try {
//                while (phase == Phase.RECEIVING_SAMPLES) {
//                    if (socket.socket != null && socket.socket.isConnected()) {
//                        String data = socket.receive();
//                        if (data != null && !data.isEmpty()) {
//                            char first = data.charAt(0);
//                            if (first == EOT) {
//                                System.out.println("INFINITY: orders complete, preparing results...");
//                                phase = Phase.SENDING_RESULTS;
//                            } else {
//                                socket.send("" + ACK);
//                            }
//                        }
//                    }
//                    sleep(200);
//                }
//            } catch (Exception e) {
//                System.out.println("INFINITY receivingOrders error: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).start();
//    }

    // ---------------------------------------------------------------------------
    // Phase 2 – Send results to driver
    // Sends all 45,977 frames from the real log in one transmission.
    // ---------------------------------------------------------------------------

//    private void sendingResults() {
//        new Thread(() -> {
//            try {
//                while (phase != Phase.SENDING_RESULTS) {
//                    sleep(500);
//                }
//                // Simulate instrument processing time
//                sleep(3000);
//                sendingFrames(resultFrames);
//                phase = Phase.DONE;
//            } catch (Exception e) {
//                System.out.println("INFINITY sendingResults error: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }).start();
//    }

    // ---------------------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------------------

    private String[] loadFrames(String resourceName) {
        List<String> frames = new ArrayList<>();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream(resourceName);
            if (is == null) {
                System.err.println("INFINITY: resource not found: " + resourceName);
                return new String[0];
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.isEmpty()) {
                        frames.add(line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("INFINITY: error loading frames: " + e.getMessage());
            e.printStackTrace();
        }
        return frames.toArray(new String[0]);
    }
}
