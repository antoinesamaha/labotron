package com.neofoc.app.drivers.cobas501;

import com.foc.Globals;
import com.neofoc.app.driver.DriverSerialPort;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.LabTest;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.util.Iterator;

public class Cobas501FrameCreator extends AstmFrameCreator {

	public Cobas501FrameCreator() {
	}

	/*
	 * @Override public AstmFrame newHeaderFrame(Instrument instrument, int
	 * sequence) { AstmFrame frame = new AstmFrame(instrument, sequence,
	 * AstmFrame.FRAME_TYPE_HEADER);
	 * 
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * 
	 * frame.append2Data(AstmFrame.REPEAT_DELIMITER);
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
	 * frame.append2Data(AstmFrame.ESCAPE_DELIMITER);
	 * 
	 * for (int i = 0; i < 3; i++) {
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); }
	 * frame.append2Data("host"); frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data("cobas6000");
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data("TSREQ^REAL");
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); frame.append2Data("P");
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); frame.append2Data("1");
	 * 
	 * return frame; }
	 * 
	 * public AstmFrame newPatientFrame(Instrument instrument, int sequence, int
	 * sequence_num, String sampleId, String patientId, String firstName, String
	 * lastName, String middleName, Date dob, int age, String sex) { AstmFrame
	 * frame = new AstmFrame(instrument, sequence,
	 * AstmFrame.FRAME_TYPE_PATIENT);
	 * 
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(String.valueOf(sequence_num));
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); //
	 * frame.append2Data(patientId, 15);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); String patientId_Local =
	 * ""; try { AstmDriver driver = (AstmDriver) instrument.getDriver(); if
	 * (driver != null && driver.getAstmParams() != null) { if
	 * (driver.getAstmParams().isSendPatientIdToInstrument()) { patientId_Local
	 * = patientId; } } } catch (Exception e) { }
	 * frame.append2Data(patientId_Local, 15);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); if (dob != null &&
	 * dob.getTime() > Globals.DAY_TIME) { SimpleDateFormat sdf = new
	 * SimpleDateFormat("yyyyMMdd"); frame.append2Data(sdf.format(dob));
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER); } else {
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER); }
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); // Sex Field if (sex !=
	 * null) { frame.append2Data(sex); }
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); // In this case send the
	 * age only if (age > 0) { frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
	 * frame.append2Data("" + age);
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER); frame.append2Data("Y");
	 * } else { frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
	 * frame.append2Data(AstmFrame.COMPONENT_DELIMITER); }
	 * 
	 * return frame; }
	 * 
	 * public AstmFrame newCommentFrame(Instrument instrument, int sequence, int
	 * sequence_num, L3Sample sam) throws Exception { AstmFrame frame = new
	 * AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_COMMENT);
	 * 
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(String.valueOf(sequence_num));
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); frame.append2Data("I");
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data(sam.getFirstName() + " " + sam.getLastName());
	 * frame.append2Data(AstmFrame.FIELD_SEPERATOR); frame.append2Data("G");
	 * return frame; }
	 * 
	 * public AstmFrame newLastFrame(Instrument instrument, int sequence, int
	 * sequence_num) { AstmFrame frame = super .newLastFrame(instrument,
	 * sequence, sequence_num); frame.append2Data(AstmFrame.FIELD_SEPERATOR);
	 * frame.append2Data('F'); return frame; }
	 */
	public void scanSampleTestsAndBuildFrames(AstmDriver driver, FocLabSample sam,
			boolean fromDriver) throws Exception {
		AstmFrame frame = new AstmFrame(driver.getInstrument(),
				getNextSequence(), AstmFrame.FRAME_TYPE_ORDER);

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data(String.valueOf(1));
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data(sam.getId(), 15);
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		// DIFF frame.append2Data(specimen, 15);
		frame.append2Data(AstmFrame.FIELD_SEPERATOR);

		boolean firstTest = true;
		Iterator tIter = sam.testIterator();
		while (tIter != null && tIter.hasNext()) {
			FocLabTest test = (FocLabTest) tIter.next();
			if (test != null) {
				if (!firstTest) {
					frame.append2Data(AstmFrame.REPEAT_DELIMITER);
				}
				for (int i = 0; i < 3; i++) {
					frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
				}
				String instrCode = ((DriverSerialPort) driver)
						.testMaps_getInstCode(test.getLabel());
				frame.append2Data(instrCode,
						getTestCodeLength(driver.getInstrument()));
				// frame.append2Data(AstmFrame.COMPONENT_DELIMITER);
				// frame.append2Data('0');
			}
			firstTest = false;
		}

		frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		frame.append2Data('R');

		for (int i = 0; i < 6; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}
		frame.append2Data('N');
		for (int i = 0; i < 14; i++) {
			frame.append2Data(AstmFrame.FIELD_SEPERATOR);
		}
		frame.append2Data('O');
		driver.addFrame(frame);
	}

	public String getFirstnameLastnameIn_P_Frame(FocLabSample sam){
//		sam.getFirstName() + " "
//		+ sam.getLastName()
		return "";
	}
	
	public void buildFrameArray(AstmDriver driver, L3Message message,
			boolean fromDriver) throws Exception {

		// ENQ frame
		// -----------
		AstmFrame frame = newEnquiryFrame(driver.getInstrument());
		driver.addFrame(frame);
		// -----------

		// BIG frame
		frame = new AstmFrame(driver.getInstrument(), 1,
				AstmFrame.FRAME_TYPE_CONTINUITY);

		frame.append2Data("H|\\^&|||host^2||||||TSDWN^BATCH");
		frame.append2Data(AstmFrame.CR);

		// Patient Frame
		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			FocLabSample sam = (FocLabSample) sIter.next();
			if (sam != null) {
				// Patient
				//B-PatientID-20151123
				//frame.append2Data("P|1||PatID|||||");
				String patientID = sam != null ? sam.getPatientId() : null;
				if(patientID == null || patientID.equals("")) patientID = "PatID";
				
				//20160129-B-Infinity Wants the first name last name in the P frame
				frame.append2Data("P|1||"+patientID+"||");
				String firstLastName_In_P_Frame = getFirstnameLastnameIn_P_Frame(sam);
				if(firstLastName_In_P_Frame != null && firstLastName_In_P_Frame.length() > 0) frame.append2Data(firstLastName_In_P_Frame);
				frame.append2Data("|||");
				//20160129-E
				
				//E-PatientID-20151123
				if (sam.getSexe().compareTo("F") == 0) {
					frame.append2Data("F||||||");
				} else {
					frame.append2Data("M||||||");
				}
				if (sam.getAge() > 0 && sam.getAge() < 150) {
					frame.append2Data(sam.getAge()+"");
				}
				
				if(driver == null || driver.getAstmParams() == null || driver.getAstmParams().isPutYForAGE()){
					frame.append2Data("^Y");
				}
				// -------
				frame.append2Data(AstmFrame.CR);

				String liquidTypeString = "1";
				switch (sam.getLiquidType()) {
				case FocLabSample.LIQUID_TYPE_CSF:
					liquidTypeString = "3";
					break;
				case FocLabSample.LIQUID_TYPE_BODY_FLUID:
					liquidTypeString = "5";
					break;
				case FocLabSample.LIQUID_TYPE_SERUM:
					liquidTypeString = "1";
					break;
				case FocLabSample.LIQUID_TYPE_URIN:
					liquidTypeString = "2";
					break;
				case FocLabSample.LIQUID_TYPE_STOOL:
				case FocLabSample.LIQUID_TYPE_OTHERS:
					liquidTypeString = "5";
					break;
				case FocLabSample.LIQUID_TYPE_SUPERNATENT:
					liquidTypeString = "4";
					break;
				}

				// Order
				frame.append2Data("O|1|" + sam.getId() + "|" + sam.getId()
						+ "^^^^S" + liquidTypeString + "^SC|");

				boolean isFirst = true;
				Iterator tIter = sam.testIterator();
				while (tIter != null && tIter.hasNext()) {
					FocLabTest test = (FocLabTest) tIter.next();
					String machineTest = test != null ? driver.testMaps_getInstCode(test.getLabel()) : null;
					if (machineTest != null) {
						// Order Frame
						if (!isFirst) {
							frame.append2Data("\\");
						}
						frame.append2Data("^^^" + machineTest + "^");
						isFirst = false;
						// ---------------
						// if (fromDriver == false){
						// Result Frame
						// frame = AstmFrame.newResultFrame(getInstrument(),
						// getNextSequence(), 1, test.getLabel(),
						// test.getUnitLabel(), test.getValue());
						// addFrame(frame);
						// ----------------
						// }
					}
				}
				frame.append2Data("|R||");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				if (sam.getEntryDate().getTime() < 24 * 60 * 60 * 1000) {
					Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"
							+ sam.getEntryDate().getTime());
					frame.append2Data(sdf.format(Globals.getApp()
							.getSystemDate()));
				} else {
					frame.append2Data(sdf.format(sam.getEntryDate()));
				}

				// frame.append2Data("||||N||||1||||||||||O");
				frame.append2Data("||||N||||" + liquidTypeString
						+ "||||||||||O");
				frame.append2Data(AstmFrame.CR);

				//20160129-B Infinity Wants the first name last name in the P frame
				if(firstLastName_In_P_Frame != null && !firstLastName_In_P_Frame.isEmpty()){
					frame.append2Data("C|1|L|^^^^|G");
				}else{
				//20160129-E
					frame.append2Data("C|1|L|" + sam.getFirstName() + " " + sam.getLastName() + "^^^^|G");
				//20160129-B Infinity Wants the first name last name in the P frame					
				}
				//20160129-E
				// frame.append2Data("C|1|L|C1^C2^C3^C4^C5|G");

				frame.append2Data(AstmFrame.CR);
			}
			frame.append2Data("L|1|N");
			frame.append2Data(AstmFrame.CR);
		}

		addAndSplitDataFrame(driver, frame);

		// Last frame
		// frame = AstmFrame.newLastFrame(getInstrument(), getNextSequence(),
		// 1);
		// addFrame(frame);
		// -------------

		// EOT frame
		frame = newEndOfTransmissionFrame(driver.getInstrument());
		driver.addFrame(frame);
		// -------------
	}

	private static final int FRAME_SIZE = 240;

	protected void addAndSplitDataFrame(AstmDriver driver, AstmFrame frame) {
		AstmFrame remainingFrame = frame;
		int sequence = 1;
		while (remainingFrame.getData().length() > FRAME_SIZE) {
			AstmFrame splitFrame = new AstmFrame(driver.getInstrument(),
					sequence++, AstmFrame.FRAME_TYPE_CONTINUITY);
			splitFrame.setIntermediateFrame(true);
			splitFrame.setData(new StringBuffer(remainingFrame.getData()
					.substring(0, FRAME_SIZE)));
			driver.addFrame(splitFrame);
			remainingFrame.getData().replace(0, FRAME_SIZE, "");
		}

		remainingFrame.setSequence(sequence);
		driver.addFrame(remainingFrame);
	}

}
