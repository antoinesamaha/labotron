package com.neofoc.app.drivers.octa;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class OctaDriver extends AstmDriver {
	public OctaDriver(){
		super();
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new OctaFrameCreator();
		
		getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
		getAstmParams().setTestCodeLength(3);
		getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
		getAstmParams().setConcatenatedFrames(true);
		getAstmParams().setReadComment3(true);
		getAstmParams().setReadResultComment(true);
		getAstmParams().setTreatHigherLessAlarmSeparately(false);
	}
	
	@Override
	public void init(FocInstrument instrument, Properties props) throws Exception {
		Globals.logString("Init OctaDriver");
		if(props != null){
			Globals.logString("OctaDriver Set to TCPIP");
			props.put("tcpip", "1");
		}
		super.init(instrument, props);

		OctaFrame answerFrame = new OctaFrame(instrument);
		getL3SerialPort().setAnswerFrame(answerFrame);
	}

	public boolean isInquiryBased() {
		return true;
	}

	protected void initDriverReceiver() {
		Globals.logString("OctaDriver initDriverReceiver");
		setDriverReceiver(new OctaReceiver(this));
	}
}

