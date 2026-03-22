package com.neofoc.app.drivers.bcs;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class BCSDriver extends AstmDriver {
	public BCSDriver(){
		super();
		frameCreator = new BCSFrameCreator();
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
		setDriverReceiver(new BCSReceiver(this));
	}
}
