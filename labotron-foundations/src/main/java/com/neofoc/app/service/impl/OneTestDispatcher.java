package com.neofoc.app.service.impl;

import com.foc.Globals;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocTestLabelMap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class OneTestDispatcher {
	private DispatcherServiceImpl dispatcher          = null;
	private ArrayList<FocTestLabelMap> testLabelArray      = null;
	
	public OneTestDispatcher(DispatcherServiceImpl dispatcher){
		this.dispatcher = dispatcher;
		testLabelArray  = new ArrayList<FocTestLabelMap>();
	}
	
	public void dispose(){
		if(testLabelArray != null){
			testLabelArray.clear();
		}
		testLabelArray = null;
		dispatcher = null;
	}
	
	public void addTestLabelMap(FocTestLabelMap testLabelMap){
		if(testLabelArray != null && testLabelMap != null){
			testLabelArray.add(testLabelMap);
		}
	}
	
	public FocTestLabelMap getFirstActive(){
		FocTestLabelMap testLabel = null;
		
		for(int i=0; i<testLabelArray.size() && testLabel == null; i++){
			FocTestLabelMap currTestLabel = testLabelArray.get(i);

			FocInstrument instr = (FocInstrument) currTestLabel.getPropertyObject("instrument");
			if(!instr.getPropertyBoolean("on_hold") && !currTestLabel.getPropertyBoolean("on_hold")){
				testLabel = currTestLabel ;
			}
		}
		return testLabel;
	}

	public FocTestLabelMap findTestLabelMapForInstrument(String instrumentCode){
		FocTestLabelMap foundTestLabel = null;

		for(int i=0; i<testLabelArray.size() && foundTestLabel == null; i++){
			FocTestLabelMap currTestLabel = testLabelArray.get(i);
			FocInstrument instr = (FocInstrument) currTestLabel.getPropertyObject("instrument");
			if(instr.getCode().compareTo(instrumentCode) == 0){
				foundTestLabel = currTestLabel ;
			}
		}
		return foundTestLabel;
	}

	public void sortAccordingToStatus(){
//		int status = dispatcher.getStatus();
			
		String field = "day_priority";
//		switch(status){
//		case DispatcherServiceImpl.STATUS_HOLIDAY:
//			field = TestLabelMapDesc.FLD_HOLIDAY_PRIORITY;
//			break;
//		case DispatcherServiceImpl.STATUS_NIGHT:
//			field = TestLabelMapDesc.FLD_NIGHT_PRIORITY;
//			break;
//		}
		
		final String fldToUse = field;

		Collections.sort(testLabelArray, new Comparator<FocTestLabelMap>(){
			public int compare(FocTestLabelMap o1, FocTestLabelMap o2) {
				return o1.getPropertyInteger(fldToUse) - o2.getPropertyInteger(fldToUse);
			}
		});
		
		for(int i=0; i<testLabelArray.size(); i++){
			FocTestLabelMap map = testLabelArray.get(i);
			FocInstrument instr = (FocInstrument) map.getPropertyObject("instrument");
			if (instr != null) {
				Globals.logDetail("Instr in order (" + i + "): " + instr.getPropertyString("code"));
			} else {
				Globals.logDetail("Instr in order (" + i + "): IS NULL !!" );
			}
		}
	}
}
