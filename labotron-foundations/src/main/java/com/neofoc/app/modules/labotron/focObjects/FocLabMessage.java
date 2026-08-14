package com.neofoc.app.modules.labotron.focObjects;

import com.foc.ConfigInfo;
import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocObjectGeneral;
import com.neofoc.app.driver.MessageListener;
import com.neofoc.app.modules.labotron.LabMessage;
import com.neofoc.app.modules.labotron.LabMessage_FocObject;

public class FocLabMessage extends LabMessage_FocObject implements MessageListener {

    public static final String FLD_INSTRUMENT = "instrument";

    public FocLabMessage(FocConstructor constr) {
        super(constr);
    }

    @Override
    public void messageReceived(L3Message message) {

    }

    public boolean isEmulator() {
        return false;
    }

    public void logException(Exception e) {
        Globals.logString(getName() + "->Exception");
        Globals.logException(e);
    }

    public synchronized void logString(String str) {
        Globals.logString(getName() + "->" + str);
    }

    public synchronized void logString(StringBuffer str) {
        if (ConfigInfo.isLogDetails() || !isEmulator()) {
            Globals.logString(getName() + "->" + str);
        }
    }
}
