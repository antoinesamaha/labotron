package com.neofoc.app.impl;

import com.neofoc.app.SimSocket;

import java.net.ServerSocket;
import java.net.Socket;

/**
 * Simulator for GEM Premier 3500 Blood Gas Analyzer
 *
 * The real GEM 3500 acts as TCP server on port 1182 and ASTM master:
 * it pushes results to whoever connects, never receives orders.
 *
 * This simulator binds a ServerSocket, waits for Labotron's
 * PhysicalClientSocket to connect, then pushes one result batch
 * as ASTM master (ENQ → frames → EOT) and closes the connection.
 * Labotron reconnects automatically for the next batch.
 */
public class GemPremier3500Simulator extends AbstractSimulator {

    public static final int GEM_TCP_PORT = 1182;

    // Frames modeled on GEM Premier 3500 Interface Protocol doc (section 3.5.11 patient sample example).
    // H: F2=delimiters, F5=6-component instrument info, F6-F13=null, F14=datetime
    // P: F4=patient_id, F6=last^first, F8=dob, F9=sex
    // O: F3=specimen_id, F4-F15=null, F16=A (arterial sample type)
    // R1: F11=operator, F12=null, F13=datetime (only on first R record; subsequent Rs are minimal)
    // Parameter names per GEM spec: Na+, K+, Ca++, HCO3-, BEecf, SO2c
    // L: only F1 and F2 per spec
    String[] GEM_PREMIER_3500_ENQUITY_FRAMES = {
        "1H|\\^&|||GEM 3500^V1.0^12345^^^1.00|||||||||20260210120000",
        "2Q|1|123123||||||||||D",
        "3L|1"
    };

    String[] GEM_PREMIER_3500_RESULT_FRAMES = {
            "1H|\\^&|||GEM 3500^V1.0^12345^^^1.00|||||||||20260210120000",
            "2P|1||12345||Doe^John||19850315|M",
            "3O|1|SAMPLE001|||||||||||||A",
            "4R|1|^^^pH|7.38|||||||admin||20260210120100",
            "5R|2|^^^pCO2|42|mmHg",
            "6R|3|^^^pO2|95|mmHg",
            "7R|4|^^^HCO3-|24.5|mmol/L",
            "0R|5|^^^BEecf|0.5|mmol/L",
            "1R|6|^^^SO2c|98.2|%",
            "2R|7|^^^Na+|142|mmol/L",
            "3R|8|^^^K+|4.2|mmol/L",
            "4R|9|^^^Ca++|1.25|mmol/L",
            "5R|10|^^^Glu|95|mg/dL",
            "6R|11|^^^Lac|1.5|mmol/L",
            "7R|12|^^^Hct|42|%",
            "0L|1"
    };

    @Override
    public void simulate() {
        System.out.println("GEM Premier 3500 Simulator: binding TCP server on port " + GEM_TCP_PORT);

        try (ServerSocket serverSocket = new ServerSocket(GEM_TCP_PORT)) {
            //while (true) {
                System.out.println("GEM Premier 3500 Simulator: waiting for Labotron to connect...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("GEM Premier 3500 Simulator: Labotron connected from " + clientSocket.getRemoteSocketAddress());

                // Wrap the accepted socket in SimSocket so we can reuse sendingFrames()
                socket = new SimSocket(this, GEM_TCP_PORT);
                socket.socket = clientSocket;

                //sendingFrames(GEM_PREMIER_3500_RESULT_FRAMES);
                sendingFrames(GEM_PREMIER_3500_ENQUITY_FRAMES);

                clientSocket.close();
                socket = null;

                System.out.println("GEM Premier 3500 Simulator: batch sent, waiting for next connection...");
                sleep(2000);
             //}
        } catch (Exception e) {
            System.out.println("GEM Premier 3500 Simulator error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
