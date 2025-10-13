package com.neofoc.app;

import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SimSocket {

    String host = "localhost";
    int port = 12345;         // Replace with the target port
    boolean connected = false;

    SimulatorMain sim;
    Socket socket;

    SimSocket(SimulatorMain sim, int port) {
        this.port = port;
        this.sim = sim;
    }

    public void open() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // Keep trying to connect every 2 seconds until successful and change the phase
                //while (sim.phase.compareTo(Phase.OPENING_SOCKET) <= 0) {
                while (true) {
                    try {
                        //if (!connected) {
                        if (!connected || socket == null || !socket.isConnected()) {
                            socket = new Socket(host, port);
                            connected = true;
                            sim.phase = Phase.RECEIVING_SAMPLES;
                        }
                    } catch (Exception e) {
                        connected = false;
                        System.out.println("Error opening socket: " + e.getMessage());
                        e.printStackTrace();
                    }

                    sim.sleep(2000);
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
            System.out.println("Message sent: " + message);
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
                System.out.println("Message received: " + message);
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
