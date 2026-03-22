package com.neofoc.app.drivers.bcs;

import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class BCSFrame extends AstmFrame {

	public BCSFrame(FocInstrument instrument, int sequence, char type) {
		super(instrument, sequence, type);
	}

	public BCSFrame(FocInstrument instrument) {
		super(instrument);
	}
}