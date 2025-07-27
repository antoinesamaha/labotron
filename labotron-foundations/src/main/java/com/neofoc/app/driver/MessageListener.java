package com.neofoc.app.driver;

import com.neofoc.app.modules.labotron.focObjects.L3Message;

public interface MessageListener {
    void messageReceived(L3Message message);
}

