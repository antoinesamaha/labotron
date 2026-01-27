package com.neofoc.app.connection.basicsocket;

public interface BService {
    String SEND_PING = "PING";
    String SEND_SWITCH_ON = "ON";
    String SEND_SWITCH_OFF = "OFF";
    String SEND_EXIT = "EXIT";
    String SEND_VIOLENT_EXIT = "VIOLENT_EXIT";

    String REPLY_SUCCESS = "SUCCESS";
    String REPLY_FAILED = "FAIL";

    String getName();

    boolean isOn();

    boolean switchOn();

    boolean switchOff();

    boolean exit();

    boolean violentExit();

    boolean ping();

    boolean launch();

    void dispose();
}
