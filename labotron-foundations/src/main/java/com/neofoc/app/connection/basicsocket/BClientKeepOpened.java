package com.neofoc.app.connection.basicsocket;

public class BClientKeepOpened {

    private String serverIP = "localhost";     // server IP name
    private int serverPort = 3456;            // server port number
    private LogInterface logInterface = null;

    private java.net.Socket sock = null; // Socket object for communicating
    private java.io.PrintWriter pw = null; // socket output to server
    private java.io.BufferedReader br = null; // socket input from server

    public BClientKeepOpened(String serverIP, int serverPort) {
        this.serverIP = serverIP;
        this.serverPort = serverPort;
    }

    public void dispose() {
        serverIP = null;
        logInterface = null;
    }

    public void setLogInterface(LogInterface logInterface) {
        this.logInterface = logInterface;
    }

    protected void logString(String str) {
        if (logInterface != null) {
            logInterface.logString(str);
        }
    }

    protected void logException(Exception e) {
        if (logInterface != null) {
            logInterface.logException(e);
        }
    }

    public void open() {
        try {
            sock = new java.net.Socket(serverIP, serverPort);       // create socket and connect
            pw = new java.io.PrintWriter(sock.getOutputStream(), true);  // create reader and writer
            br = new java.io.BufferedReader(new java.io.InputStreamReader(sock.getInputStream()));
        } catch (Exception e) {
            logString(" Could not open socker to: " + serverIP + ":" + serverPort);
            logException(e);
        }
    }

    public void close() {
        try {
            if (pw != null) pw.close();
            if (br != null) br.close();
            if (sock != null) sock.close();
        } catch (Exception e) {
            logString(" Could not close socket : " + serverIP + ":" + serverPort);
            logException(e);
        }
    }

    public String sendMessage(String messageToSend) {
        String answer = null;

        try {
            logString(" To " + serverIP + ":" + serverPort + " send : " + messageToSend);
            if (pw == null) {
                logString(" Could not send because Print Writer was null");
            } else if (br == null) {
                logString(" Could not send because Bufferred reader was null");
            } else {
                pw.println(messageToSend);// send msg to the server
                logString(" Sent");

                char[] charsRead = new char[2000];
                int nbrOfChars = br.read(charsRead);// get data from the server
                if (nbrOfChars > 0) {
                    answer = new String(charsRead, 0, nbrOfChars);
                }
                logString(" From " + serverIP + ":" + serverPort + " receive : " + answer);
            }
        } catch (Exception e) {
            logString(" Could not send '" + messageToSend + "' to " + serverIP + ":" + serverPort);
            logException(e);
        }
        return answer;
    }
}
