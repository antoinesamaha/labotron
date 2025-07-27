package com.neofoc.app.connection.basicsocket;

import java.net.ServerSocket;

public class BServer implements Runnable {
    private int port = -1;
    private ServerSocket socket = null;
    private BServerListener serverListener = null;
    private Thread thread = null;
    private LogInterface logInterface = null;

    public BServer(int port, BServerListener mailBoxListener) {
        this.port = port;
        this.serverListener = mailBoxListener;
    }

    public BServer(int port) {
        this(port, null);
    }

    public void dispose() {
        closeSocket();
        serverListener = null;
        thread = null;
        logInterface = null;
    }

    public int getPort() {
        return port;
    }

    public ServerSocket getSocket() {
        return socket;
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

    public boolean openSocket() {
        boolean error = true;
        try {
            if (socket == null) {
                socket = new ServerSocket(port);
                error = socket == null;
            } else {
                error = false;
            }
        } catch (Exception e) {
            logException(e);
            error = true;
        }
        return error;
    }

    public void closeSocket() {
        try {
            if (socket != null) {
                socket.close();
                socket = null;
            }
        } catch (Exception e) {
            logException(e);
        }
    }

    public boolean startPolling() {
        boolean error = openSocket();
        if (!error) {
            thread = new Thread(this);
            thread.start();
        }
        return error;
    }

    public BServerListener getServerListener() {
        return serverListener;
    }

    public void setServerListener(BServerListener mailBoxListener) {
        this.serverListener = mailBoxListener;
    }

    public void run() {
        while (true) {
            try {
                logString("Before looping for socket.accept() at port:" + port);
                java.net.Socket clientSocket = socket.accept();// wait for
                // client to
                // connect
                logString("At " + port + " New client");
                ClientSocketHandler csh = new ClientSocketHandler(clientSocket);
                csh.start();
            } catch (Exception e) {
                logString("At " + port + " EXCEPTION");
                logException(e);
            }
        }
    }

    public class ClientSocketHandler extends Thread {

        java.net.Socket clientSocket = null;

        public ClientSocketHandler(java.net.Socket clientSocket) {
            this.clientSocket = clientSocket;
        }

        public void run() {
            try {
                java.io.PrintWriter pw = new java.io.PrintWriter(clientSocket.getOutputStream(), true);
                java.io.BufferedReader br = new java.io.BufferedReader(new java.io.InputStreamReader(clientSocket.getInputStream()));

                String msg = br.readLine();// read msg from client
                logString("At " + port + " received : " + msg);

                String msg2Send = null;
                if (serverListener != null) {
                    msg2Send = serverListener.replyToReceivedRequest(msg);
                    logString("At " + port + " reply : " + msg2Send);
                    pw.println(msg2Send); // send msg to client
                }
                logString("At " + port + " after condition");

                pw.close(); // close everything
                br.close();
                clientSocket.close();
                clientSocket = null;

                if (serverListener != null) {
                    serverListener.postReply();
                }
            } catch (Exception e) {
                logString("At " + port + " EXCEPTION");
                logException(e);
            }
        }
    }
}
