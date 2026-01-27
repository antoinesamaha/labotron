package com.neofoc.app.connection.basicsocket;

public class BClient {

    private String serverIP = "localhost";     // server IP name
    private int serverPort = 3456;            // server port number
    private LogInterface logInterface = null;

    public BClient(String serverIP, int serverPort) {
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

    public String sendMessage(String messageToSend) {
        String answer = null;
        java.net.Socket sock = null;                              // Socket object for communicating
        java.io.PrintWriter pw = null;                              // socket output to server
        java.io.BufferedReader br = null;                              // socket input from server

        try {
            sock = new java.net.Socket(serverIP, serverPort);       // create socket and connect
            pw = new java.io.PrintWriter(sock.getOutputStream(), true);  // create reader and writer
            br = new java.io.BufferedReader(new java.io.InputStreamReader(sock.getInputStream()));

            logString("To " + serverIP + ":" + serverPort + " send : " + messageToSend);
            pw.println(messageToSend);                      // send msg to the server
            answer = br.readLine();                              // get data from the server
            logString("From " + serverIP + ":" + serverPort + " receive : " + answer);

            pw.close();                                                 // close everything
            br.close();
            sock.close();
        } catch (Exception e) {
            logString(" Could not send '" + messageToSend + "' to " + serverIP + ":" + serverPort);
            logException(e);
        }
        return answer;
    }
}
