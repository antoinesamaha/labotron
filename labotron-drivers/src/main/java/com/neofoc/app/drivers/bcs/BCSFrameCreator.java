package com.neofoc.app.drivers.bcs;

import com.foc.Globals;
import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class BCSFrameCreator extends AstmFrameCreator {
	public BCSFrameCreator() {
	}

	public void dispose() {
	}

	/*
	 * public AstmFrame newOrderFrame (Instrument instrument, int sequence,
	 * L3Sample sample, String testString) throws Exception{ BCTFrame frame =
	 * new BCTFrame(instrument, sequence, AstmFrame.FRAME_TYPE_NONE);
	 * 
	 * frame.append2Data("J ");
	 * 
	 * //BARCODE String sampleID = sample.getId();//
	 * +"-"+sample.getFirstName()+"-"+sample.getLastName();
	 * frame.append2Data(sampleID, 23); frame.append2Data(ASCII.SPACE);
	 * 
	 * frame.append2Data(sample.getFirstName()+"__"+sample.getLastName());
	 * 
	 * frame.append2Data(ASCII.SPACE); frame.append2Data("N");
	 * frame.append2Data(ASCII.SPACE);
	 * 
	 * 
	 * 
	 * // // /////////////////////// // SimpleDateFormat sdf = new
	 * SimpleDateFormat("dd/MM/yyyy"); // if(sample.getEntryDate().getTime() <
	 * 24*60*60*1000){ //
	 * Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"
	 * +sample.getEntryDate().getTime()); //
	 * frame.append2Data(sdf.format(Globals.getApp().getSystemDate())); //
	 * }else{ // frame.append2Data(sdf.format(sample.getEntryDate())); // } //
	 * // frame.append2Data(","); // // sdf = new SimpleDateFormat("HH:mm"); //
	 * frame.append2Data(sdf.format(sample.getEntryDate())); //
	 * frame.append2Data(ASCII.SPACE); // /////////////
	 * 
	 * // frame.append2Data("1"); // additionnal request //
	 * frame.append2Data(ASCII.SPACE);
	 * 
	 * frame.append2Data(testString); return frame; }
	 */

	public AstmFrame newOrderFrame(FocInstrument instrument, int sequence, int sequence_num, String specimen, String testId, ArrayList<String> testArrayList, Date collectionDate, String priority)
			throws Exception {
		AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_ORDER);

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data(String.valueOf(sequence_num));
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data(specimen, 15);
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data(specimen, 15);
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);

		if (testArrayList != null) {
			for (int i = 0; i < testArrayList.size(); i++) {
				if (i > 0){
					frame.append2Data(AstmFrame.REPEAT_DELIMITER);
				}
				appendASingleTest(frame, testArrayList.get(i), getTestCodeLength(instrument));
			}

		} else {
			String instrCode = ((DriverSerialPort) instrument.getDriver()).testMaps_getInstCode(testId);
			appendASingleTest(frame, instrCode, getTestCodeLength(instrument));
			// for (int i=0; i<3; i++){
			// frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
			// }
			// String instrCode =
			// ((DriverSerialPort)instrument.getDriver()).testMaps_getInstCode(testId);
			// frame.append2Data(instrCode, getTestCodeLength(instrument));
		}

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		if (priority != null)
			frame.append2Data(priority, 1);
		for (int i = 0; i < 2; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		if (collectionDate.getTime() < 24 * 60 * 60 * 1000) {
			frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
		} else {
			frame.append2Data(sdf.format(collectionDate));
		}

		for (int i = 0; i < 4; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}
		frame.append2Data('N');
		for (int i = 0; i < 14; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}
		frame.append2Data('O');
		return frame;
	}

}