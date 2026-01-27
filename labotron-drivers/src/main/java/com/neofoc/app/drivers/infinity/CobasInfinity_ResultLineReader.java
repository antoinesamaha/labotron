package com.neofoc.app.drivers.infinity;

import com.foc.Globals;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.ResultLineReader;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;

import java.util.StringTokenizer;

public class CobasInfinity_ResultLineReader extends ResultLineReader {
	
	public static final String[] infinityAlarms = {
		"",
		"ADC.E",
		">Cuvet",
		"Samp.S",
		"Reag.S",
		">Abs",
		">Proz",
		">React",
		">React",
		">React",
		">Lin",//10
		">Lin",
		"S1A.E",
		"Dup.E",
		"Std.E",
		"Sens.E",
		"Cal.E",
		"SD.E",
		"ISE.N",
		"ISE.E",
		"Slop.E",//20
		"Prep.E",
		"IStd.E",
		"<>Test",
		"CmpT.E",
		"CmpT.?",
		">Test",
		"<Test",
		"R4SD",
		"S2-2Sa",
		"S2-2Sw",//30
		"S4-1Sa",
		"S4-1Sw",
		"S10Xa",
		"S10Xw",
		"Q3SD",
		"Q2.5SD",
		"ClcT.E",
		"Over.E",
		"Calc.?",
		"n.a.",//40
		"n.a.",
		"Edited",
		"Cal.E",
		">Rept",
		"<Rept",
		"Samp.?",
		"",
		"",
		"",
		"",//50	
		"Rsp1.E",
		"Rsp2.E",
		"Cond.E",
		"",
		"",
		">Kin",
		">Index",
		">Index",
		"Mix.E",
		"<Mix",//60
		"",
		"SysR.S",
		">AB",
		"AB.E",
		">Curr",
		"Curr.E",
		"Samp.H",
		"Samp.B",
		"Reag.H",
		"Reag.F",//70
		"CarOvr",
		"Samp.C",
		"Det.S",
		"Reag.T",
		"Inc.T",
		"SysR.T",
		"Cell.T",//77
		"",
		"",
		"",
		"",
		"",
		"Samp.O",//83
		"",
		"",
		"SLLD.E",
		"SLLD.N",
		"",
		"",
		"",//90
		"",
		"",
		"WB.T",
		"WB.S",
		"Clot.E",
		"Clot.E",
		"Clot.E",
		"Samp.B",
		">Curr",
		"<SigL",//100
		"ReagEx",
		"",
		">I.L",
		">I.H",
		">I.I",
		">I.LH",
		">I.LI",
		">I.HI",
		">I.LHI"//109
	};
	
	public CobasInfinity_ResultLineReader(AstmDriver driver) {
		super(driver);
	}

	public void readToken(String token, int fieldPos, int compPos) {
		super.readToken(token, fieldPos, compPos);
		
		com.foc.Globals.logDetail(" fieldPos:"+fieldPos+" compPos:"+compPos+" token:"+token);

		if(fieldPos == FLD_ALARM_CODE){
			if(isDoRead()){
				FocLabTest test = getTest();

				try{
					if(token != null && token.length() > 0 && test != null){
						//if(token.contains(",")) {
							String message = "";
					    StringTokenizer tok = new StringTokenizer(token, ",", false);      
					    while(tok.hasMoreTokens()){
					    	String currentMessage = null;
					      
					    	String subToken = tok.nextToken();
					      try {
					      	int alarmCode = Integer.valueOf(subToken);
									if(alarmCode > 0 && alarmCode < infinityAlarms.length){
										currentMessage = infinityAlarms[alarmCode];
									}
					      } catch(Exception e) {
					      	//currentMessage = subToken;
					      }

					      if (currentMessage != null && !currentMessage.isEmpty()) {
					      	if(!message.isEmpty()) message += ", ";
					      	message += currentMessage;
					      }
					    }
					    test.setNotificationMessage(message);
							
//						} else {
//							int alarmCode = Integer.valueOf(token);
//							if(alarmCode > 0 && alarmCode < infinityAlarms.length){
//								String message = infinityAlarms[alarmCode];
//								test.setNotificationMessage(message);
//							}
//							
//						}
					}
				}catch(Exception e){
					Globals.logExceptionWithoutPopup(e);
				}
			}
		}
	}
}
