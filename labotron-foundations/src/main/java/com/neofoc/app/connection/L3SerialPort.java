package com.neofoc.app.connection;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.util.ASCII;
import com.neofoc.app.connection.socket.SocketPort;
import com.neofoc.app.driver.L3ConfigInfo;
import com.neofoc.app.driver.L3Frame;
import com.neofoc.app.exceptions.L3TimeOutException;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Properties;

public class L3SerialPort {
    private SerialPortInterface serialPort = null;
    private L3Frame answerFrame = null;
    private boolean newAnswerAvailable = false;
    private ArrayList<L3SerialPortListener> listenerList = null;
    private boolean synchronousMessage = false;
    private long startWaitingForNextResultFrame = 0;
    private long maximumTimeWaitingForNextResultFrame = 0;
    private long lastActivityTime = 0;

    // private TimerExpectingAnotherFrame timerExpectingAnotherFrame = null;

    private L3SerialPortReceptionCumulationBuffer cumulationListener = null;

    public L3SerialPort() {
        super();
        serialPort = null;
        answerFrame = null;
        cumulationListener = new L3SerialPortReceptionCumulationBuffer();
    }

    public void dispose() {
        closeConnection();
        // timerExpectingAnotherFrame = null;

        if (serialPort != null) {
            serialPort.dispose();
            serialPort = null;
        }

        if (cumulationListener != null) {
            cumulationListener.dispose();
            cumulationListener = null;
        }
    }

    public boolean isSynchronousMessage() {
        return synchronousMessage;
    }

    private void resetLastActivityTime() {
        lastActivityTime = System.currentTimeMillis();
    }

    public boolean shouldResetConnection() { // h min sec millis
        return false;// (System.currentTimeMillis() - lastActivityTime) > (1 *
        // 10 * 60 * 1000);
    }

    public boolean isSerialPortNull() {
        return serialPort == null || serialPort.isSerialPortNull();
    }

    public void logString(String str) {
        if (answerFrame != null) {
            answerFrame.getInstrument().logString(str);
        } else {
            Globals.logString(str);
        }
    }

    public void logException(Exception e) {
        if (answerFrame != null) {
            answerFrame.getInstrument().logException(e);
        } else {
            Globals.logException(e);
        }
    }

    public synchronized void logSerialMessage(String str) {
        String newStr = ASCII.convertNonCharactersToDescriptions(str);

        if (answerFrame != null && answerFrame.getInstrument() != null) {
            answerFrame.getInstrument().logString(newStr);
        } else {
            Globals.logString(newStr);
        }
    }

    public long getMaximumTimeWaitingForNextResultFrame() {
        return maximumTimeWaitingForNextResultFrame;
    }

    public void setMaximumTimeWaitingForNextResultFrame(long maximumTimeWaitingForNextResultFrame) {
        this.maximumTimeWaitingForNextResultFrame = maximumTimeWaitingForNextResultFrame;
    }

    public void setAnswerFrame(L3Frame answerFrame) {
        this.answerFrame = answerFrame;
    }

    public void addListener(L3SerialPortListener listener) {
        if (listenerList == null) {
            listenerList = new ArrayList<L3SerialPortListener>();
        }
        listenerList.add(listener);
    }

    public void removeListener(L3SerialPortListener listener) {
        if (listenerList != null) {
            listenerList.remove(listener);
        }
    }

    public InputStream getInputStream() throws Exception {
        return serialPort.getInputStream();
    }

    public OutputStream getOutputStream() throws Exception {
        return serialPort.getOutputStream();
    }

    public boolean isEmulationMode() {
        return L3ConfigInfo.getEmulationMode();
    }

    public void setParametersFromProperties(Properties props) throws Exception {
        if (serialPort == null) {
            if (props != null && props.get("tcpip") != null && props.get("tcpip").equals("1")) {
                try {
                    serialPort = new SocketPort(props);
                } catch (Exception e) {
                    Globals.logException(e);
                }
            } else {
                throw new Exception("Driver must be TCPIP based");
            }
        }
    }

    public void appendAvailableDataToBuffer(StringBuffer cumulatedBuffer) throws Exception {
        if (cumulatedBuffer != null) {
            InputStream inputStream = serialPort.getInputStream();
            while (inputStream.available() > 0) {
                int theByte = inputStream.read();
                if (theByte >= 0) {
                    // l3SerialPort.logString("DataAv->byte = "+(char)theByte+" ascii"+theByte);
                    cumulatedBuffer.append((char) theByte);
                } else {
                    break;
                }
            }
        }
    }

    public void openConnection() throws Exception {
        serialPort.openConnection();
        cumulationListener.setL3SerialPort(this);
        serialPort.addEventListener(cumulationListener);
        resetLastActivityTime();
    }

    public void closeConnection() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.closeConnection();
        }
    }

    public boolean isLogBufferDetails() {
        return ConfigInfo.isLogDetails();// (b01.foc.ConfigInfo.isLogConsoleActive()
        // ||
        // b01.foc.ConfigInfo.isLogFileActive())
        // && answerFrame != null &&
        // answerFrame.getInstrument() !=
        // null &&
        // answerFrame.getInstrument().isLogBufferDetails();
    }

    public synchronized boolean extractAnswerFromBuffer(StringBuffer buffer) {
        boolean ok = false;
        resetLastActivityTime();

        if (ConfigInfo.isLogDetails() && buffer.toString() != null && buffer.toString().length() > 0) {
            logSerialMessage("...Buff+->" + buffer.toString());
        }

        if (answerFrame != null) {
            ok = answerFrame.extractAnswerFromBuffer(buffer);
            if (ok) {
                boolean callAgain = answerFrame.isDoNotAnswerThatFrameBecauseReceivedAnotherMessage();

                logSerialMessage("Received->" + answerFrame.getDataWithFrame().toString());
                newAnswerAvailable = true;
                startWaitingForNextResultFrame = 0;

                if (synchronousMessage) {
                    synchronousMessage = false;
                    if (isLogBufferDetails()) {
                        Globals.logString("Notify :" + this.toString());
                    }
                    notify();
                } else {
                    for (int i = 0; listenerList != null && i < listenerList.size(); i++) {
                        L3SerialPortListener listener = listenerList.get(i);
                        listener.received(answerFrame);
                    }
                }
                if (callAgain) {
                    //In this case we need to answer the following frames too. This was done for PentraML.
                    Globals.logString("Calling the extractAnswerFromBuffer again.");
                    extractAnswerFromBuffer(buffer);
                }
            }
        }
        return ok;
    }

    public synchronized L3Frame sendAndWaitForResponse(String strOut, long timeOutMilliseconds) throws Exception {
        resetLastActivityTime();
        if (isLogBufferDetails()) {
            for (int i = 0; i < strOut.length() && i < 3; i++) {
                answerFrame.logString(" sending char[" + i + "] : " + (int) strOut.charAt(i));
            }
        }

        OutputStream outputStream = null;
        newAnswerAvailable = false;
        L3Frame answerFrameClone = null;

        try {
            outputStream = serialPort.getOutputStream();
            if (outputStream != null) {
                synchronousMessage = true;
                logSerialMessage("Sending(" + timeOutMilliseconds + ")->" + strOut);
                Globals.logString("Before Sending with timeout");
                outputStream.write(strOut.getBytes());
                Globals.logString("After Sending with timeout");
            }
            if (isLogBufferDetails())
                Globals.logString("Wait :" + this.toString());
            wait(timeOutMilliseconds);
            if (isLogBufferDetails())
                Globals.logString("Wait Out:" + this.toString());
            synchronousMessage = false;
            if (newAnswerAvailable) {
                newAnswerAvailable = false;
                answerFrameClone = (L3Frame) answerFrame.clone();
            } else {
                String newStr = "Timed out while waiting for response to :" + ASCII.convertNonCharactersToDescriptions(strOut);
                throw new L3TimeOutException(newStr);
            }
        } catch (L3TimeOutException timeOutException) {
            throw timeOutException;
        } catch (Exception e) {
            synchronousMessage = false;
            throw new Exception("Error while sending string : " + strOut, e);
        }
        return answerFrameClone;
    }

    public synchronized void send(String strOut) throws Exception {
        resetLastActivityTime();
        OutputStream outputStream = null;

        outputStream = serialPort.getOutputStream();
        if (outputStream != null) {
            logSerialMessage("Sending->" + strOut);
            Globals.logString("Before Sending");
            outputStream.write(strOut.getBytes());
            Globals.logString("After Sending");
            startWaitingForNextResultFrame = System.currentTimeMillis();
        }
    }

    public boolean isWaitingForNextResultFrameTimedOut() {
        boolean timedOut = false;
        if (startWaitingForNextResultFrame > 0 && maximumTimeWaitingForNextResultFrame > 0) {
            timedOut = (System.currentTimeMillis() - startWaitingForNextResultFrame) > maximumTimeWaitingForNextResultFrame;
        }
        return timedOut;
    }
}