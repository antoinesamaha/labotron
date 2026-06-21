package com.neofoc.app.connection.socket;

import com.foc.Globals;
import com.neofoc.app.connection.SerialPortInterface;
import com.neofoc.app.exceptions.L3SerialPortOpeningException;

import javax.comm.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

public class SocketPort implements SerialPortInterface {
    private PhysicalSocket       physicalSocket             = null;
    private PhysicalSocket       physicalSocketReceptionOnly = null;//Some Drivers require separate ports for Send Receive
    private PhysicalClientSocket physicalClientSocket       = null;

    public SocketPort(Properties props) throws Exception {
        setParametersFromProperties(props);
    }

    public void dispose() {
        closeConnection();
        if (physicalSocket != null) {
            physicalSocket.dispose();
            physicalSocket = null;
        }
        if (physicalSocketReceptionOnly != null) {
            physicalSocketReceptionOnly.dispose();
            physicalSocketReceptionOnly = null;
        }
        if (physicalClientSocket != null) {
            physicalClientSocket.dispose();
            physicalClientSocket = null;
        }
    }

    public boolean isClientMode() {
        return physicalClientSocket != null;
    }

    public PhysicalSocket getPhysicalSocket() {
        return physicalSocket;
    }

    public boolean isConnected() {
        if (isClientMode()) {
            return physicalClientSocket.isConnected();
        }
        if (physicalSocket == null) {
            return false;
        }
        return physicalSocket.isConnected();
    }

    public PhysicalSocket getPhysicalSocketReceiPhysicalSocket() {
        return physicalSocketReceptionOnly;
    }

    public boolean isSerialPortNull() {
        return physicalSocket == null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        //Not Needed for Socket Ports
        // return serialPort.getInputStream();
        return null;
    }

    @Override
    public OutputStream getOutputStream() throws IOException {
        if (isClientMode()) {
            return physicalClientSocket.getOutputStream();
        }
        OutputStream out = null;
        if (getPhysicalSocket() != null) {
            out = getPhysicalSocket().getOutputStream();
        }
        return out;
    }

    @Override
    public void setParametersFromProperties(Properties props) throws Exception {
        if (props != null) {
            int portNbtInt = -1;
            String portNbr = props.getProperty("serialPort.name");
            if (portNbr != null && portNbr.compareTo("") != 0) {
                portNbtInt = Integer.valueOf(portNbr);
            }

            boolean instrumentIsAstmMaster = "true".equals(props.getProperty("instrumentIsAstmMaster"));
            if (instrumentIsAstmMaster) {
                String remoteHost = props.getProperty("tcpip.remoteHost");
                Globals.logString("Client mode: connecting to instrument at " + remoteHost + ":" + portNbtInt);
                physicalClientSocket = new PhysicalClientSocket(remoteHost, portNbtInt);
            } else {
                physicalSocket = new PhysicalSocket(portNbtInt);

                //Checking if 2 separate ports for send/receive
                //---------------------------------------------
                Globals.logString("Checking if we need 2 TCPIP ports");
                if (props.get("tcpip") != null && props.get("tcpip").equals("2")) {
                    portNbtInt = portNbtInt + 1;
                    Globals.logString("Opening additional port at : " + (portNbtInt));
                    physicalSocketReceptionOnly = new PhysicalSocket(portNbtInt);
                }
                //---------------------------------------------
            }
        }
    }

    @Override
    public void closeConnection() {
        if (physicalClientSocket != null) {
            physicalClientSocket.closeSocket();
        }
        if (physicalSocket != null) {
            physicalSocket.closeSocket();
        }
        if (physicalSocketReceptionOnly != null) {
            physicalSocketReceptionOnly.closeSocket();
        }
    }

    @Override
    public void openConnection() throws L3SerialPortOpeningException {
        if (isClientMode()) {
            Globals.logString("Socket connecting to instrument (client mode)...");
            boolean error = physicalClientSocket.connect();
            if (error) {
                throw new L3SerialPortOpeningException("L3X - Could not connect to " + physicalClientSocket.getRemoteHost() + ":" + physicalClientSocket.getPort());
            }
            Globals.logString("Socket connected to instrument successfully");
        } else if (getPhysicalSocket() != null) {
            Globals.logString("Socket connecting...");
            Globals.logString("    starting to pool on port = " + getPhysicalSocket().getPort());

            boolean error = getPhysicalSocket().startPolling();
            Globals.logString("    started pooling");

            if (error) {
                throw new L3SerialPortOpeningException("L3X - Port " + physicalSocket.getPort() + " Error opening Socket");
            }
            Globals.logString("Socket connected successfuly");
        } else {
            throw new L3SerialPortOpeningException("Physical Socket is Null");
        }
    }

    /**
     * Asynchronously opens the connection without blocking the calling thread.
     * Returns a CompletableFuture that completes when the connection is established or fails.
     */
    public CompletableFuture<Void> openConnectionAsync() {
        return CompletableFuture.runAsync(() -> {
            try {
                openConnection();
            } catch (L3SerialPortOpeningException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void addEventListener(SerialPortEventListener cumulationListener) throws Exception {
        if (isClientMode()) {
            physicalClientSocket.addEventListener(cumulationListener);
        } else if (getPhysicalSocket() != null) {
            getPhysicalSocket().addEventListener(cumulationListener);
        }
    }

    @Override
    public void removeEventListener() {
        if (isClientMode()) {
            physicalClientSocket.removeEventListener();
        } else if (getPhysicalSocket() != null) {
            getPhysicalSocket().removeEventListener();
        }
    }
}
