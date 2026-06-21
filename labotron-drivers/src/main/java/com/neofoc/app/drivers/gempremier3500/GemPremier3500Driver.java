package com.neofoc.app.drivers.gempremier3500;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

/**
 * Driver for GEM Premier 3500 Blood Gas Analyzer
 *
 * Protocol: Standard ASTM E1394-91 and E1381-91
 * Communication: TCP/IP (GEM acts as Server/Master)
 * Interface: Bidirectional - GEM can send results and receive orders
 */
public class GemPremier3500Driver extends AstmDriver {

    public GemPremier3500Driver() {
        super();

        // Configure ASTM parameters based on GEM Premier 3500 specifications
        getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
        getAstmParams().setTestCodeLength(4); // GEM uses 4-character test codes
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setConcatenatedFrames(true);
        getAstmParams().setReadComment3(false);
        getAstmParams().setReadResultComment(false);
        getAstmParams().setTreatHigherLessAlarmSeparately(false);
        getAstmParams().setTakeAllFramesFromBufferNotJustTheLast(true);
        getAstmParams().setInstrumentIsAstmMaster(true);
        getAstmParams().setDoNotSendOrdersBecauseOneWay(true);
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        // GEM Premier 3500 uses TCP/IP communication
        if (props != null) {
            props.put("tcpip", "1");
            props.put("instrumentIsAstmMaster", "true");
            props.put("tcpip.remoteHost", instrument.getRemoteHost());
        }
        super.init(instrument, props);
    }
}
