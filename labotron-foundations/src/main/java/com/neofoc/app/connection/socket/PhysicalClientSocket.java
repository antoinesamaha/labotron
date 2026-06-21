package com.neofoc.app.connection.socket;

import com.foc.Globals;
import com.neofoc.app.connection.L3SerialPortReceptionCumulationBuffer;

import javax.comm.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class PhysicalClientSocket implements Runnable {

    private final String remoteHost;
    private final int    remotePort;

    private Socket clientSocket = null;
    private Thread thread       = null;
    private ArrayList<L3SerialPortReceptionCumulationBuffer> listenerArray = new ArrayList<>();

    public PhysicalClientSocket(String remoteHost, int remotePort) {
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    public void dispose() {
        closeSocket();
        if (listenerArray != null) {
            listenerArray.clear();
            listenerArray = null;
        }
        thread = null;
    }

    public boolean isConnected() {
        return clientSocket != null && clientSocket.isConnected() && !clientSocket.isClosed();
    }

    public int getPort() {
        return remotePort;
    }

    public String getRemoteHost() {
        return remoteHost;
    }

    public boolean connect() {
        boolean error = false;
        try {
            clientSocket = new Socket(remoteHost, remotePort);
            Globals.logString("PhysicalClientSocket connected to " + remoteHost + ":" + remotePort);
            thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            Globals.logString("PhysicalClientSocket failed to connect to " + remoteHost + ":" + remotePort);
            Globals.logException(e);
            error = true;
        }
        return error;
    }

    public void closeSocket() {
        if (clientSocket != null) {
            try {
                clientSocket.close();
            } catch (IOException e) {
                Globals.logException(e);
            }
            clientSocket = null;
        }
    }

    public OutputStream getOutputStream() {
        OutputStream out = null;
        try {
            if (clientSocket != null) {
                out = clientSocket.getOutputStream();
            }
        } catch (IOException e) {
            Globals.logException(e);
        }
        return out;
    }

    public void addEventListener(SerialPortEventListener listener) {
        if (listenerArray != null && listener instanceof L3SerialPortReceptionCumulationBuffer) {
            listenerArray.add((L3SerialPortReceptionCumulationBuffer) listener);
        }
    }

    public void removeEventListener() {
        if (listenerArray != null) {
            listenerArray.clear();
        }
    }

    @Override
    public void run() {
        while (true) {
            if (clientSocket == null) {
                reconnect();
                continue;
            }
            try {
                InputStreamReader streamReader = new InputStreamReader(clientSocket.getInputStream());
                char[] cArray = new char[1000];

                while (true) {
                    StringBuffer incrementalBuffer = new StringBuffer();
                    int nbrOfCharacters = streamReader.read(cArray);
                    if (nbrOfCharacters < 0) {
                        break; // EOF: GEM closed after this batch
                    }
                    for (int i = 0; i < nbrOfCharacters; i++) {
                        incrementalBuffer.append(cArray[i]);
                    }
                    notifyListeners(incrementalBuffer);
                }

                Globals.logString("PhysicalClientSocket: GEM closed connection, will reconnect");
            } catch (Exception e) {
                Globals.logString("PhysicalClientSocket at " + remoteHost + ":" + remotePort + " connection lost");
                Globals.logException(e);
            }

            closeSocket();
            clientSocket = null;
            reconnect();
        }
    }

    private void reconnect() {
        while (clientSocket == null) {
            try {
                Globals.logString("PhysicalClientSocket reconnecting to " + remoteHost + ":" + remotePort + "...");
                Thread.sleep(5000);
                clientSocket = new Socket(remoteHost, remotePort);
                Globals.logString("PhysicalClientSocket reconnected to " + remoteHost + ":" + remotePort);
            } catch (Exception e) {
                Globals.logString("PhysicalClientSocket reconnection failed, will retry in 5s...");
                Globals.logException(e);
                clientSocket = null;
            }
        }
    }

    private void notifyListeners(StringBuffer buffer) {
        if (listenerArray == null) return;
        for (L3SerialPortReceptionCumulationBuffer listener : listenerArray) {
            listener.cumulateBufferAndAttemptToExtractFrame(buffer);
        }
    }
}
