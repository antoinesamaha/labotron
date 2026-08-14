/*
 * Created on Jun 14, 2006
 */
package com.neofoc.app.drivers.bcs;

import com.neofoc.app.drivers.astm.AstmReceiver;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

/**
 * @author 01Barmaja
 */
public class BCSReceiver extends AstmReceiver {

	public BCSReceiver(BCSDriver driver){
		super(driver);
	}
	
	@Override
	protected FocLabTest addTest(FocLabSample sample, String lisTestCode){
		FocLabTest test = super.addTest(sample, lisTestCode);
		if(test != null){
			test.setRoundingPrecision(0.01);
		}
		return test;
	}
}