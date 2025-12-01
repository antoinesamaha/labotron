package com.neofoc.app;

import com.neofoc.app.impl.ISimulator;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SimSocket {

    String host = "localhost";
    int port = 12345;         // Replace with the target port
    boolean connected = false;
    boolean sendingMode = false;

    ISimulator sim;
    public Socket socket;

    public SimSocket(ISimulator sim, int port) {
        this.port = port;
        this.sim = sim;
    }

    public void setSendingMode(boolean sendingMode) {
        this.sendingMode = sendingMode;
    }

    public boolean isSendingMode() {
        return this.sendingMode;
    }

    public void open() {
        while (!connected) {
            try {
                //if (!connected) {
                if (socket == null || !socket.isConnected()) {
                    socket = new Socket(host, port);
                    connected = true;
                    System.out.println("Socket connected");
                }
            } catch (Exception e) {
                connected = false;
                System.out.println("Error opening socket: " + e.getMessage());
                e.printStackTrace();
            }
            sim.sleep(500);
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Keep trying to connect every 2 seconds until successful and change the phase
                //while (sim.phase.compareTo(Phase.OPENING_SOCKET) <= 0) {
                while (true) {
                    if (connected && socket != null && socket.isConnected()) {
                        // If not in sending mode I keep bouncing back ACK to anything received except EOT
                        if (!isSendingMode()) {
                            String receivedString = receive();
                            if (receivedString != null && !receivedString.isEmpty() && receivedString.charAt(0) != Constants.EOT) {
                                sim.sleep(1000);
                                send(""+Constants.ACK);
                            } else {
                                System.out.println("Socket no need to send ACK");
                            }
                        } else {
                            System.out.println("Socket in sending mode");
                        }
                    } else {
                        System.out.println("Socket not connected!!!");
                    }
                    sim.sleep(1000);
                }
            }
        }).start();
    }

    public void send(String message) {
        try {
            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);
            writer.print(message); // Changed from println() to print()
            writer.flush(); // Ensure the data is sent immediately

            String logMessage = message;
            if (message.length() == 1 && message.charAt(0) == Constants.ENQ) {
                logMessage = "ENQ";
            } else if (message.length() == 1 && message.charAt(0) == Constants.ACK) {
                logMessage = "ACK";
            } else if (message.length() == 1 && message.charAt(0) == Constants.NACK) {
                logMessage = "NACK";
            } else if (message.length() == 1 && message.charAt(0) == Constants.EOT) {
                logMessage = "EOT";
            }
            System.out.println("Message sent: " + logMessage);
        } catch (Exception e) {
            connected = false;
            e.printStackTrace();
        }
    }

    public String receive() {
        try {
            if (socket == null || !socket.isConnected()) {
                System.out.println("Socket is not connected. Cannot receive messages.");
                return null;
            }

            InputStream input = socket.getInputStream();
            StringBuilder receivedData = new StringBuilder();
            byte[] buffer = new byte[1024];
            int bytesRead;

            // Check if there's data available to read
            if (input.available() > 0) {
                // Read the available data
                bytesRead = input.read(buffer);
                if (bytesRead > 0) {
                    receivedData.append(new String(buffer, 0, bytesRead));
                }
            }

            String message = receivedData.toString();
            if (!message.isEmpty()) {
                String logMessage = message;
                if (message.length() == 1 && message.charAt(0) == Constants.ENQ) {
                    logMessage = "ENQ";
                } else if (message.length() == 1 && message.charAt(0) == Constants.ACK) {
                    logMessage = "ACK";
                } else if (message.length() == 1 && message.charAt(0) == Constants.NACK) {
                    logMessage = "NACK";
                } else if (message.length() == 1 && message.charAt(0) == Constants.EOT) {
                    logMessage = "EOT";
                }
                System.out.println("Message received: " + logMessage);
            }
            return message;
        } catch (Exception e) {
            connected = false;
            System.out.println("Error receiving message: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    public void writeToFile(String message, String fileName) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, true); // true for append mode
            java.io.BufferedWriter bufferedWriter = new java.io.BufferedWriter(fileWriter);
            PrintWriter printWriter = new PrintWriter(bufferedWriter);

            printWriter.println(message);
            printWriter.println("-----------------------------------");

            printWriter.close();
            System.out.println("Message logged to file: " + fileName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
                System.out.println("Socket closed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
