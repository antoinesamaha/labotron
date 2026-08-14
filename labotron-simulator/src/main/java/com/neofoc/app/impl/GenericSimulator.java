package com.neofoc.app.impl;

import com.neofoc.app.Constants;
import com.neofoc.app.SimSocket;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

/**
 * Configurable ASTM simulator — TCP server or client, ASTM master or slave.
 *
 * Frame sequence control
 * ----------------------
 * Pass a frames array to control the full conversation. Two kinds of entries:
 *
 *   - A normal frame string  → sent as-is in an ASTM session (ENQ → frames → EOT)
 *   - RECEIVE sentinel       → simulator hands the floor to Labotron: it ACKs every
 *                              incoming frame until Labotron sends EOT, then resumes
 *
 * After the last frame is sent the simulator automatically stays in receive mode,
 * ACKing any further sessions Labotron initiates, until the connection drops.
 *
 * Pass null (or empty array) for frames to operate as a pure slave (ACK everything).
 *
 * Example — inquiry then receive orders then send results:
 *
 *   String[] frames = {
 *       "1H|\\^&", "2Q|1|^701641||||||||||O|", "3L|1|N",  // send inquiry
 *       GenericSimulator.RECEIVE,                            // receive orders from Labotron
 *       "1H|\\^&", "2P|1||701641", "3O|1|...", "4R|1|...", "5L|1|N"  // send results
 *   };
 *   new GenericSimulator(9000, SocketRole.SERVER, frames).simulate();
 */
public class GenericSimulator extends AbstractSimulator {

    public enum SocketRole { SERVER, CLIENT }

    /** Drop this sentinel anywhere in your frames array to trigger a receive phase. */
    public static final String RECEIVE = "<<RECEIVE>>";

    // ---------------------------------------------------------------
    // Built-in sample frame arrays — edit or replace as needed
    // ---------------------------------------------------------------

    public static final String[] GENERIC_RESULT_FRAMES = {
        "1H|\\^&|6744636705355103||PSM^Roche Diagnostics^PSM^2.03.01b|||||||P||20250829101145",
        "2P|1||701641|1|||G",
        "3O|1|5565802|||||||||X||||SERUM||||||||F",
        "4R|1|^^^627|1.740|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "5O|2|5565802|||||||||X||||SERUM||||||||F",
        "6R|2|^^^587|23.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "7O|3|5565802|||||||||X||||SERUM||||||||F",
        "0R|3|^^^685|26.00|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "1L|1|N"
    };

    public static final String[] GENERIC_INQUIRY_FRAMES = {
        "1H|\\^&",
        "2Q|1|^701641||||||||||O|",
        "3L|1|N"
    };

    public static final String[] GENERIC_INQUIRY_THEN_RESULT_FRAMES = {
        "1H|\\^&",
        "2Q|1|^701641||||||||||O|",
        "3L|1|N",
        RECEIVE,
        "1H|\\^&|6744636705355103||PSM^Roche Diagnostics^PSM^2.03.01b|||||||P||20250829101145",
        "2P|1||701641|1|||G",
        "3O|1|5565802|||||||||X||||SERUM||||||||F",
        "4R|1|^^^627|1.740|||||F||bmserv^~SYSValDaemon~||20250829101112|452.1",
        "5L|1|N"
    };

    public final static String[] GEM_PREMIER_3500_FRAMES = {
        "1H|\\^&|||GEM 3500^V1.0^12345^^^1.00|||||||||20260210120000",
        "2Q|1|123123||||||||||D",
        "3L|1",
        RECEIVE,
        "1H|\\^&|||GEM 3500^V1.0^12345^^^1.00|||||||||20260210120000",
        "2P|1||123123||Doe^John||19850315|M",
        "3O|1|123123|||||||||||||A",
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

    // Maglumi: instrument is TCP server on port 6100. It sits idle until Labotron
    // connects and sends ENQ with the sample order, then pushes back the results.
    // Test codes are 3 chars (MaglumiDriver.setTestCodeLength(3)).
    public final static String[] MAGLUMI_FRAMES = {
        RECEIVE,
        "1H|\\^&|||MAGLUMI^V1.0^12345^^^1.00|||||||||20260809120000",
        "2P|1||123123||Doe^John||19850315|M",
        "3O|1|123123|123123^^^^S1^SC|||||||||||||SERUM||||||||F",
        "4R|1|^^^001|12.5|IU/mL|||N||admin||20260809120100",
        "5R|2|^^^002|3.4|ng/mL|||N||admin||20260809120100",
        "6R|3|^^^003|45.6|mIU/mL|||N||admin||20260809120100",
        "0L|1|N"
    };

    // ---------------------------------------------------------------

    private final int port;
    private final SocketRole role;
    private final String host;
    private final String[] frames;

    public GenericSimulator(int port, SocketRole role, String[] frames) {
        this(port, role, "localhost", frames);
    }

    public GenericSimulator(int port, SocketRole role, String host, String[] frames) {
        this.port = port;
        this.role = role;
        this.host = host;
        this.frames = frames;
    }

    @Override
    public void simulate() {
        if (role == SocketRole.SERVER) {
            runAsServer();
        } else {
            runAsClient();
        }
    }

    // ---------------------------------------------------------------
    // Server mode
    // ---------------------------------------------------------------

    private void runAsServer() {
        System.out.println("GenericSimulator [SERVER]: binding on port " + port);
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (true) {
                System.out.println("GenericSimulator [SERVER]: waiting for connection...");
                Socket clientSocket = serverSocket.accept();
                System.out.println("GenericSimulator [SERVER]: connected from " + clientSocket.getRemoteSocketAddress());

                socket = new SimSocket(this, port);
                socket.socket = clientSocket;

                processFrameSequence();

                clientSocket.close();
                socket = null;
                sleep(500);
            }
        } catch (Exception e) {
            System.out.println("GenericSimulator error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ---------------------------------------------------------------
    // Client mode
    // ---------------------------------------------------------------

    private void runAsClient() {
        System.out.println("GenericSimulator [CLIENT]: connecting to " + host + ":" + port);
        socket = new SimSocket(this, port);
        socket.setHost(host);
        socket.connect(); // plain TCP connect — no background thread, we manage everything below
        sleep(500);
        processFrameSequence();
    }

    // ---------------------------------------------------------------
    // Frame sequence processor
    //
    // Splits the frames array at RECEIVE sentinels, producing alternating
    // send / receive phases. Always ends with an open-ended receive loop
    // so the simulator stays alive after its last send.
    // ---------------------------------------------------------------

    private void processFrameSequence() {
        if (frames == null || frames.length == 0) {
            autoRespondLoop();
            return;
        }

        // Split frames array into segments separated by RECEIVE markers.
        // Each element in the actions list is either a String[] (send segment)
        // or the RECEIVE sentinel string (receive phase).
        List<Object> actions = new ArrayList<>();
        List<String> segment = new ArrayList<>();

        for (String frame : frames) {
            if (RECEIVE.equals(frame)) {
                if (!segment.isEmpty()) {
                    actions.add(segment.toArray(new String[0]));
                    segment = new ArrayList<>();
                }
                actions.add(RECEIVE);
            } else {
                segment.add(frame);
            }
        }
        if (!segment.isEmpty()) {
            actions.add(segment.toArray(new String[0]));
        }

        for (Object action : actions) {
            if (!isConnected()) break;
            if (RECEIVE.equals(action)) {
                receiveOneSession();
            } else {
                sendingFrames((String[]) action);
            }
        }

        // Stay alive after last frame — ACK any further Labotron sessions
        autoRespondLoop();
    }

    // ---------------------------------------------------------------
    // Receive helpers
    // ---------------------------------------------------------------

    /**
     * ACKs every incoming byte until Labotron sends EOT (one ASTM session).
     * Returns when EOT is received or the connection drops.
     */
    private void receiveOneSession() {
        System.out.println("GenericSimulator: receive mode — waiting for Labotron session...");
        while (isConnected()) {
            String received = socket.receive();
            if (received == null || received.isEmpty()) {
                sleep(100);
                continue;
            }
            char first = received.charAt(0);
            if (first == Constants.EOT) {
                System.out.println("GenericSimulator: received EOT — Labotron session ended");
                return;
            }
            sleep(100);
            socket.send("" + Constants.ACK);
        }
    }

    /**
     * Loops receiveOneSession() until the connection drops.
     * Used for pure slave mode and as the final phase after all frames are sent.
     */
    private void autoRespondLoop() {
        System.out.println("GenericSimulator: auto-respond mode active");
        while (isConnected()) {
            receiveOneSession();
        }
        System.out.println("GenericSimulator: connection closed");
    }

    private boolean isConnected() {
        return socket != null && socket.socket != null
                && socket.socket.isConnected() && !socket.socket.isClosed();
    }
}
