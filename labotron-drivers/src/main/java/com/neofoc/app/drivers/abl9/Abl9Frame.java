package com.neofoc.app.drivers.abl9;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class Abl9Frame extends AstmFrame {

    public Abl9Frame(FocInstrument instrument) {
        super(instrument);
    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        String str = buffer.toString();
        int sohIndex = str.indexOf(AstmFrame.SOH);
        int eotIndex = str.indexOf(AstmFrame.EOT);

        if (sohIndex >= 0 && eotIndex > sohIndex) {
            StringBuffer response = new StringBuffer(buffer.subSequence(sohIndex, eotIndex + 1));
            setDataWithFrame(response);
            buffer.delete(0, eotIndex + 1);
            Globals.logString("ABL9 SOH block extracted, length=" + response.length());
            return true;
        }

        return super.extractAnswerFromBuffer(buffer);
    }

    @Override
    public void extractDataFromFrame() throws Exception {
        // SOH block is intercepted in Abl9Receiver.concatenateResultFrame before this is called.
        // For all other frame types (ENQ, ACK, standard STX frames) delegate to base.
        super.extractDataFromFrame();
    }
}
