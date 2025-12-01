package com.neofoc.app.impl;

import com.neofoc.app.Int2ByteConverter;
import com.neofoc.app.Phase;
import com.neofoc.app.SimSocket;

public class InfinitySimulator extends AbstractSimulator {

    public void simulate() {
        phase = Phase.OPENING_SOCKET;

        socket = new SimSocket(this, 9000);

        //These 3 calls will run in 3 parallel threads
        socket.open();
        receivingSamples();
        sendingResults();

        socket.close();
    }

    public void receivingSamples() {

        new Thread(new Runnable() {
            public void run() {
                try {
                    while (phase.compareTo(Phase.RECEIVING_SAMPLES) <= 0) {
                        if (socket.socket != null && socket.socket.isConnected()) {
                            String response = socket.receive();

                            // If we received data, log it
                            if (response != null && !response.isEmpty()) {
                                System.out.println("receivingSamples received data: " + response);
                                if (response.charAt(0) == InfinitySimulator.EOT) {
                                    phase = Phase.SENDING_RESULTS;
                                } else {
                                    socket.send("" + InfinitySimulator.ACK);
                                }
                            } else {
                                // Just for debugging - can be removed in production
                                System.out.println("receivingSamples: No data available at this poll interval");
                            }
                        }

                        // Wait 2 seconds before polling again
                        sleep(2000);
                    }
                } catch (Exception e) {
                    System.out.println("Client error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendingResults() {

        // Start the client in a separate thread
        new Thread(new Runnable() {
            public void run() {
                try {
                    int frameAt = -1;
                    while (phase.compareTo(Phase.SENDING_RESULTS) <= 0) {
                        if (phase.compareTo(Phase.SENDING_RESULTS) == 0) {
                            if (frameAt == -1) {
                                socket.send("" + ENQ);
                            } else if (frameAt < ASTM_TEST_DATA.length) {
                                String frame = ASTM_TEST_DATA[frameAt];
                                System.out.println("Sending frame: " + frame);
                                StringBuffer sbFrameWithData = createDataWithFrame(frame);
                                socket.send(sbFrameWithData.toString());
                            } else {
                                socket.send("" + EOT);
                                phase = Phase.DONE;
                            }
                            sleep(1000);
                            char responseChar = readResponseChar();
                            if (responseChar == ACK) {
                                System.out.println("Received ACK from server");
                                frameAt++;
                            } else if (responseChar == NACK) {
                                System.out.println("Received NACK from server");
                                // Resend the same frame
                            }
                        } else {
                            sleep(1000);
                        }
                    }
                } catch (Exception e) {
                    System.out.println("Client error: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
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
