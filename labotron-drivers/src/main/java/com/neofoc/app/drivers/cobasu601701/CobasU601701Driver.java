package com.neofoc.app.drivers.cobasu601701;

import com.neofoc.app.drivers.astm.AstmReceiver;
import com.neofoc.app.drivers.cobasu601.CobasU601Driver;

public class CobasU601701Driver extends CobasU601Driver {
    public CobasU601701Driver() {
        super();
    }

    @Override
    protected void initDriverReceiver() {
        super.initDriverReceiver();
        initReceiver(1);//When 601 and 701 are hooked together we need to read the index 1 instead of the index 0
    }

    protected void initReceiver(int posForTestCode) {
        AstmReceiver astmReceiver = (AstmReceiver) getDriverReceiver();
        astmReceiver.setResultLineReader(new CobasU601701_ResultLineReader(this, posForTestCode));
    }
}
