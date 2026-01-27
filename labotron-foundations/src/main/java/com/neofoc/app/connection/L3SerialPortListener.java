package com.neofoc.app.connection;

import com.neofoc.app.driver.L3Frame;

public interface L3SerialPortListener {
    void received(L3Frame frame);
}
