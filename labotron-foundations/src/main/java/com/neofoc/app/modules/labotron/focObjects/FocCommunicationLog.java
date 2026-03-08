package com.neofoc.app.modules.labotron.focObjects;

import com.foc.desc.FocConstructor;
import com.neofoc.app.modules.labotron.CommunicationLog_FocObject;

public class FocCommunicationLog extends CommunicationLog_FocObject {

    public FocCommunicationLog(FocConstructor constr) {
        super(constr);
    }

    public void setInstrument(FocInstrument instrument) {
        setPropertyObject("instrument", instrument);
    }

    public String getBody() {
        return getPropertyString("body");
    }

    public void setBody(String json) {
        setPropertyString("body", json);
    }

}