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

    String[] GEM_PREMIER_3500_RESULT_FRAMES = {
        "1H|\\^&|||GEM Premier 3500^IL^1.0||||||TSDWN^BATCH|P|1|20260210120000",
        "2P|1|||12345||Doe^John^M||19850315|M",
        "3O|1|SAMPLE001||^^^pH^\\^^^pCO2^\\^^^pO2^\\^^^HCO3^\\^^^BE^|R||||||N||||WB|||||||O",
        "4R|1|^^^pH||7.38||7.35^7.45|N||F||admin|20260210120100|GEM3500",
        "5R|2|^^^pCO2||42|mmHg|35^45|N||F||admin|20260210120100|GEM3500",
        "6R|3|^^^pO2||95|mmHg|80^100|N||F||admin|20260210120100|GEM3500",
        "7R|4|^^^HCO3||24.5|mmol/L|22^26|N||F||admin|20260210120100|GEM3500",
        "0R|5|^^^BE||0.5|mmol/L|-2^2|N||F||admin|20260210120100|GEM3500",
        "1R|6|^^^SO2||98.2|%|95^100|N||F||admin|20260210120100|GEM3500",
        "2R|7|^^^Na||142|mmol/L|136^145|N||F||admin|20260210120100|GEM3500",
        "3R|8|^^^K||4.2|mmol/L|3.5^5.0|N||F||admin|20260210120100|GEM3500",
        "4R|9|^^^Ca||1.25|mmol/L|1.12^1.32|N||F||admin|20260210120100|GEM3500",
        "5R|10|^^^Glu||95|mg/dL|70^110|N||F||admin|20260210120100|GEM3500",
        "6R|11|^^^Lac||1.5|mmol/L|0.5^2.2|N||F||admin|20260210120100|GEM3500",
        "7R|12|^^^Hct||42|%|36^46|N||F||admin|20260210120100|GEM3500",
        "0C|1|I|Quality Control: OK|G",
        "1L|1|N"
    };

    @Override
    public void simulate() {
        System.out.println("GEM Premier 3500 Simulator: binding TCP server on port " + GEM_TCP_PORT);

        try (ServerSocket serverSocket = new ServerSocket(GEM_TCP_PORT)) {
            while (true) {
                System.out.println("GEM Premier 3500 Simulator: waiting for Labotron to connect...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("GEM Premier 3500 Simulator: Labotron connected from " + clientSocket.getRemoteSocketAddress());

                // Wrap the accepted socket in SimSocket so we can reuse sendingFrames()
                socket = new SimSocket(this, GEM_TCP_PORT);
                socket.socket = clientSocket;

                sendingFrames(GEM_PREMIER_3500_RESULT_FRAMES);

                clientSocket.close();
                socket = null;

                System.out.println("GEM Premier 3500 Simulator: batch sent, waiting for next connection...");
                sleep(2000);
            }
        } catch (Exception e) {
            System.out.println("GEM Premier 3500 Simulator error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
