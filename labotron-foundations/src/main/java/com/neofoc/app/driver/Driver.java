/*
 * Created on May 7, 2006
 */
package com.neofoc.app.driver;

import com.foc.Globals;
import com.foc.list.FocList;
import com.neofoc.app.connection.L3SerialPortListener;
import com.neofoc.app.exceptions.L3Exception;
import com.neofoc.app.modules.labotron.LabMessage;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

/**
 * @author 01Barmaja
 */
public abstract class Driver implements IDriver {
    private HashMap<MessageListener, MessageListener> listenersMap = null;
    private ArrayList<L3Frame> framesArray = null;
    private FocInstrument instrument = null;
    private L3SerialPortListener receiver = null;
    private boolean busy = false;

    public void init(FocInstrument instrument, Properties props) throws Exception {
        this.instrument = instrument;
        if (instrument != null) {
            addListener(instrument);
        }
    }

    public void dispose() {
        for (int i = 0; i < getFrameCount(); i++) {
            L3Frame frm = getFrameAt(i);
            frm.dispose();
        }
        framesArray.clear();
        framesArray = null;

        listenersMap = null;
    }

    public boolean isInquiryBased() {
        return false;
    }

    /* (non-Javadoc)
     * @see b01.l3.Driver#isBusy()
     */
    public boolean isBusy() {
        return busy;
    }

    protected void resetBusy() {
        busy = false;
    }
  /*public void setBusy(boolean busy) {
    this.busy = busy;
  }*/

    protected boolean releaseWhenReceiveENQ() {
        return false;
    }

    public boolean reserve() {
        return reserve(false);
    }

    public synchronized boolean reserve(boolean enqReception) {
        boolean error = false;

        if (isBusy()) {
            if (enqReception && releaseWhenReceiveENQ()) {
                Globals.logString(getInstrument().getName() + " Driver Busy But Will be Released Because Received ENQ");
                release();
            } else {
                Globals.logString(getInstrument().getName() + " Driver Busy could not Reserve");
                error = true;
            }
        }

        if (!error) {
            Globals.logString(getInstrument().getName() + " Driver Available");
            this.busy = true;
            error = false;
        }

        return error;
    }

    protected void releasing() {

    }

    public synchronized void release() {
        if (isBusy()) {
            this.busy = false;
            releasing();
        }
    }

    public FocInstrument getInstrument() {
        return instrument;
    }

    public L3SerialPortListener getDriverReceiver() {
        return receiver;
    }

    public void setDriverReceiver(L3SerialPortListener driverReceiver) {
        this.receiver = driverReceiver;
    }

    public int getFrameCount() {
        return framesArray != null ? framesArray.size() : 0;
    }

    public void addFrame(L3Frame frame) {
        if (framesArray == null) {
            framesArray = new ArrayList<L3Frame>();
        }
        framesArray.add(frame);
    }

    public L3Frame getFrameAt(int i) {
        return framesArray != null ? (L3Frame) framesArray.get(i) : null;
    }

    public void resetFrameArray() {
        if (framesArray != null) {
            for (int i = 0; i < getFrameCount(); i++) {
                L3Frame frm = getFrameAt(i);
                frm.dispose();
            }
            framesArray.clear();
        }
    }

    public void addListener(MessageListener driverListener) {
        if (listenersMap == null) {
            listenersMap = new HashMap<MessageListener, MessageListener>();
        }
        listenersMap.put(driverListener, driverListener);
    }

    public void removeListener(MessageListener driverListener) {
        if (listenersMap != null) {
            listenersMap.remove(driverListener);
        }
    }

    public void notifyListeners(L3Message message) {
        if (listenersMap != null && message != null && message.getNumberOfSamples() != 0) {
            Iterator iter = listenersMap.values().iterator();
            while (iter != null && iter.hasNext()) {
                MessageListener listener = (MessageListener) iter.next();
                if (listener != null) {
                    listener.messageReceived(message);
                }
            }
        }
    }

    public void outputFrames(PrintStream out) {
        if (out != null) {
            out.println("number of frame = " + getFrameCount());
            for (int i = 0; i < getFrameCount(); i++) {
                L3Frame frame = getFrameAt(i);
                if (frame != null) {
                    out.println(frame.getData());
                }
            }
        }
    }

    public void completeListOfAvailableTests(FocList testList) throws Exception {
        throw new L3Exception("Method not implemented for this driver");
    }
}