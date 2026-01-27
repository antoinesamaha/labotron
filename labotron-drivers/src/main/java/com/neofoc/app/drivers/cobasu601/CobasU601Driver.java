package com.neofoc.app.drivers.cobasu601;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmReceiver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class CobasU601Driver extends AstmDriver {
    public CobasU601Driver() {
        super();
        if (frameCreator != null) {
            frameCreator.dispose();
            frameCreator = null;
        }
        frameCreator = new CobasU601FrameCreator();

        getAstmParams().setResultFrame_ComponentPositionForResultType(1);
        getAstmParams().setTestCodeLength(3);
        getAstmParams().setIgnoreLastTestCodeDigit(false);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setSendCommentFrameFromHost(false);
        //20160129-B
        getAstmParams().setDoNotSendOrdersBecauseOneWay(true);
        //20160129-E
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        if (props != null) {
            props.put("tcpip", "1");
        }
        super.init(instrument, props);
    }

    @Override
    protected void initDriverReceiver() {
        super.initDriverReceiver();
        initReceiver(0);
    }

    protected void initReceiver(int posForTestCode) {
        AstmReceiver astmReceiver = (AstmReceiver) getDriverReceiver();
        astmReceiver.setResultLineReader(new CobasU601_ResultLineReader(this, posForTestCode));
    }
}
