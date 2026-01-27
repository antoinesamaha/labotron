package com.neofoc.app.connection;

import com.foc.Globals;

import javax.comm.SerialPortEvent;
import javax.comm.SerialPortEventListener;
import java.io.InputStream;

public class L3SerialPortReceptionCumulationBuffer implements SerialPortEventListener {

    private L3SerialPort l3SerialPort = null;
    private StringBuffer cumulBuffer = null;

    public L3SerialPortReceptionCumulationBuffer() {
        this.l3SerialPort = null;
        cumulBuffer = new StringBuffer();
    }

    public void dispose() {
        l3SerialPort = null;
        cumulBuffer = null;
    }

    public void setL3SerialPort(L3SerialPort l3SerialPort) {
        this.l3SerialPort = l3SerialPort;
    }

    public void cumulateBufferAndAttemptToExtractFrame(StringBuffer incrementalBuffer) {
        if (incrementalBuffer != null) {
            cumulBuffer.append(incrementalBuffer);
            //l3SerialPort.logString("DataAvailable->"+cumulBuffer);
            l3SerialPort.extractAnswerFromBuffer(cumulBuffer);
        }
    }

    protected synchronized void dataAvailable() {
        try {
            InputStream inputStream = l3SerialPort.getInputStream();
            StringBuffer incrementalBuffer = new StringBuffer();
            while (inputStream.available() > 0) {
                int theByte = inputStream.read();
                if (theByte >= 0) {
                    //l3SerialPort.logString("DataAv->byte = "+(char)theByte+" ascii"+theByte);
                    incrementalBuffer.append((char) theByte);
                } else {
                    break;
                }
            }
            cumulateBufferAndAttemptToExtractFrame(incrementalBuffer);
        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    /* (non-Javadoc)
     * @see javax.comm.SerialPortEventListener#serialEvent(javax.comm.SerialPortEvent)
     */
    public void serialEvent(SerialPortEvent event) {
        if (l3SerialPort.isLogBufferDetails()) {
            Globals.logString("PT4: In Serial Event");
        }
        switch (event.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY: {
                break;
            }
            case SerialPortEvent.DATA_AVAILABLE: {
                dataAvailable();
            }
            break;
        }
    }
}