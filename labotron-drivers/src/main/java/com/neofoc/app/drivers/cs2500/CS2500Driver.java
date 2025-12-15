package com.neofoc.app.drivers.cs2500;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class CS2500Driver extends AstmDriver {
    public CS2500Driver() {
        super();
        if (frameCreator != null) {
            frameCreator.dispose();
            frameCreator = null;
        }
        frameCreator = new CS2500FrameCreator();

        getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
        getAstmParams().setTestCodeLength(3);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setConcatenatedFrames(false);
        getAstmParams().setReadComment3(true);
        getAstmParams().setReadResultComment(true);
        getAstmParams().setTreatHigherLessAlarmSeparately(false);
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        if (props != null) {
            props.put("tcpip", "1");
        }
        super.init(instrument, props);
    }

    public boolean isInquiryBased() {
        return true;
    }

}

