package com.neofoc.app.drivers.vitekbci;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class VitekBCIDriver extends AstmDriver {
    public VitekBCIDriver() {
        super();
        if (frameCreator != null) {
            frameCreator.dispose();
            frameCreator = null;
        }
        frameCreator = new VitekBCIFrameCreator();
        getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
        getAstmParams().setConcatenatedFrames(true);
        //getAstmParams().setTestCodeLength(3);
        //getAstmParams().setIgnoreLastTestCodeDigit(true);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
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
        setDriverReceiver(new VitekBCIReceiver(this));
    }

    @Override
    protected void initAnswerFrame() {
        VitekBCIFrame answerFrame = new VitekBCIFrame(getInstrument());
        getL3SerialPort().setAnswerFrame(answerFrame);
    }

}
