package com.neofoc.app.drivers.infinity;

import com.neofoc.app.drivers.cobas501.Cobas501Driver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class CobasInfinityDriver extends Cobas501Driver {
	public CobasInfinityDriver(){
		super();
		
		if(frameCreator != null){
			frameCreator.dispose();
			frameCreator = null;
		}
		frameCreator = new CobasInfinityFrameCreator();	
		
		getAstmParams().setPutYForAGE(true);
		getAstmParams().setConcatenatedFrames(false);
//		getAstmParams().setConcatenatedFrames(true);
	}
	
	@Override
	public void init(FocInstrument instrument, Properties props) throws Exception {
		if(props != null){
			props.put("tcpip", "1");
		}
		super.init(instrument, props);
	}
	
	@Override
	protected void initDriverReceiver() {
		CobasInfinity_AstmReceiver astmReceiver = new CobasInfinity_AstmReceiver(this);
		setDriverReceiver(astmReceiver);
		if(astmReceiver != null){
			astmReceiver.setResultLineReader(new CobasInfinity_ResultLineReader(this));
			astmReceiver.setCommentResultReader(new CobasInfinity_CommentResultReader());
		}
	}
}
