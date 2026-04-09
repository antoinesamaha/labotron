package com.neofoc.app.impl;

import com.neofoc.app.Constants;
import com.neofoc.app.Int2ByteConverter;
import com.neofoc.app.Phase;
import com.neofoc.app.SimSocket;

public abstract class AbstractSimulator implements ISimulator, Constants {

    public SimSocket socket;
    public Phase phase;//0 receiving samples, 1 sending results

    public Phase getPhase() {
        return phase;
    }

    public void setPhase(Phase phase) {
        this.phase = phase;
    }

    public SimSocket getSocket() {
        return socket;
    }

    public void setSocket(SimSocket socket) {
        this.socket = socket;
    }

    public void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            System.out.println("Polling interrupted: " + e.getMessage());
        }
    }

    public void sendingFrames(String[] frames) {
        this.sendingFrames(frames, false);
    }

    public void sendingFrames(String[] frames, boolean oneShot) {
        socket.setSendingMode(true);

        try {
            String oneShotString = "";
            if (oneShot) oneShotString = "" + ENQ;
            else {
                socket.send("" + ENQ);
                boolean waitingForEnqAck = true;
                while (waitingForEnqAck) {
                    char resp = readResponseChar();
                    if (resp == ACK || resp == NACK) {
                        waitingForEnqAck = false;
                        System.out.println("Initial ENQ response: " + resp);
                    }
                }
            }

            for (int i=0; i<frames.length; i++) {
                String frame = frames[i];

                // New ASTM session detected: end current session, sleep, start new one
                if (i > 0 && frame.startsWith("1H|")) {
                    socket.send("" + EOT);
                    sleep(500);
                    socket.send("" + ENQ);
                    // Block until ACK/NACK for ENQ before sending any frames
                    boolean waitingForEnqAck = true;
                    while (waitingForEnqAck) {
                        char resp = readResponseChar();
                        if (resp == ACK || resp == NACK) {
                            waitingForEnqAck = false;
                            System.out.println("New session ENQ response: " + resp);
                        }
                    }
                }

                System.out.println("Sending frame: " + frame);
                StringBuffer sbFrameWithData = createDataWithFrame(frame);

                if (oneShot) oneShotString += sbFrameWithData.toString();
                else socket.send(sbFrameWithData.toString());

                sleep(100);

                boolean keepLooping = true;
                while(keepLooping) {
                    char responseChar = readResponseChar();
                    if (responseChar == ACK) {
                        keepLooping = false;
                        System.out.println("Received ACK from server");
                    } else if (responseChar == NACK) {
                        keepLooping = false;
                        System.out.println("Received NACK from server");
                    } else {
                        keepLooping = true;
                        System.out.println("Need to wait mode reveived: " + responseChar);
                    }
                }
            }

            if (oneShot) {
                oneShotString += "" + EOT;
                socket.send(oneShotString);
            }
            else socket.send("" + EOT);

        } catch (Exception e) {
            System.out.println("Client error: " + e.getMessage());
            e.printStackTrace();
        }

        socket.setSendingMode(false);
    }

    public StringBuffer createDataWithFrame(String data) {
        StringBuffer buffer = new StringBuffer();

        buffer.append(STX);

        StringBuffer bufferForChecksum = new StringBuffer(data);
        bufferForChecksum.append(CR);
        bufferForChecksum.append(ETX);
        char[] checkSum = computeChecksum(bufferForChecksum);

        buffer.append(bufferForChecksum);
        buffer.append(checkSum[0]);
        buffer.append(checkSum[1]);
        buffer.append(CR);
        buffer.append(LF);

        return buffer;
    }

    public char[] computeChecksum(StringBuffer strBuffer) {
        char parity[] = new char[2];

        byte bt[] = strBuffer.toString().getBytes();

        int sum = 0;
        for (int i = 0; i < bt.length; i++) {
            // Globals.logString("byte ="+bt[i]+" char="+ct[i]);
            sum += bt[i];
            // Globals.logString(i+" "+c+" "+bt[i]);
        }
        int mod = sum % 256;

        Int2ByteConverter conv = new Int2ByteConverter(mod);
        parity[0] = conv.getHighByte();
        parity[1] = conv.getLowByte();

        // Globals.logString("parity: " + mod+" -> "+strBuffer);
        return parity;
    }

    public char readResponseChar() {
        String response = socket.receive();
        if (response != null && !response.isEmpty()) {
            System.out.println("Client received data: " + response);
            socket.writeToFile(response, "socket_log.txt");
            return response.charAt(0);
        }
        return SINGLE_CHAR_NOT_FOUND;
    }

}
