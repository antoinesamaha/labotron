package com.neofoc.app.connection;

import com.foc.Globals;

public class L3VirtualSerialPortReceptionCumulationBuffer extends L3SerialPortReceptionCumulationBuffer {

    protected synchronized void dataAvailable() {
        try {
        } catch (Exception e) {
            Globals.logException(e);
        }
    }
}
