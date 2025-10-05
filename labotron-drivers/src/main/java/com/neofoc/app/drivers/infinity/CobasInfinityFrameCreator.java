package com.neofoc.app.drivers.infinity;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.cobas501.Cobas501FrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.util.Iterator;

public class CobasInfinityFrameCreator extends Cobas501FrameCreator {

	@Override
	public String getFirstnameLastnameIn_P_Frame(FocLabSample sam){
		return sam != null ? sam.getFirstName() + "^" + sam.getLastName() : "";
	}
	
	public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
		int mySequence = 0;
//		resetSequence();
		
		// ENQ frame
		// -----------
//		getNextSequence();
		AstmFrame frame = newEnquiryFrame(driver.getInstrument());
		driver.addFrame(frame);
		// -----------

		// BIG frame
		mySequence++;
		if(mySequence == 8) mySequence = 1;
		frame = new AstmFrame(driver.getInstrument(), mySequence, AstmFrame.FRAME_TYPE_CONTINUITY);

		frame.append2Data("H|\\^&|||host^2||||||TSDWN^BATCH");
		
		//Add Frame
//		frame.append2Data(AstmFrame.CR);
		driver.addFrame(frame);
		mySequence++;
		if(mySequence == 8) mySequence = 1;
		frame = new AstmFrame(driver.getInstrument(), mySequence, AstmFrame.FRAME_TYPE_CONTINUITY);
		//---------

		// Patient Frame
		Iterator sIter = message.sampleIterator();
		while (sIter != null && sIter.hasNext()) {
			FocLabSample sam = (FocLabSample) sIter.next();
			if (sam != null) {
				// Patient
				//B-PatientID-20151123
				//frame.append2Data("P|1||PatID|||||");
				String origin = sam != null ? sam.getOrigin() : "";
				if(origin == null) origin = "";
				
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

				//Putting the DOB not the AGE
				if(sam.getDateOfBirth() != null){
					SimpleDateFormat sdfDOB = new SimpleDateFormat("yyyyMMdd");
					String dobString = sdfDOB.format(sam.getDateOfBirth());
					frame.append2Data(dobString);
				}
				//---------------------------
				
				//Putting the Age not the DOB
				/*
				if (sam.getAge() > 0 && sam.getAge() < 150) {
					frame.append2Data(sam.getAge()+"");
				}
				if(driver == null || driver.getAstmParams() == null || driver.getAstmParams().isPutYForAGE()){
					frame.append2Data("^Y");
				}
				*/
				// --------------------------
				
				//Add Frame
//				frame.append2Data(AstmFrame.CR);
				driver.addFrame(frame);
				mySequence++;
				if(mySequence == 8) mySequence = 1;
				frame = new AstmFrame(driver.getInstrument(), mySequence, AstmFrame.FRAME_TYPE_CONTINUITY);
				//---------
				
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

				String priority = "R";
				
				boolean isFirst = true;
				Iterator tIter = sam.testIterator();
				while (tIter != null && tIter.hasNext()) {
					FocLabTest test = (FocLabTest) tIter.next();
					if(test != null && test.getPriority() != null && test.getPriority().equals("S")) {
						priority = "S";
					}
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
				frame.append2Data("|");
				frame.append2Data(priority);//R or S
				frame.append2Data("||");

				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				if (sam.getEntryDate().getTime() < 24 * 60 * 60 * 1000) {
					Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"
							+ sam.getEntryDate().getTime());
					frame.append2Data(sdf.format(Globals.getApp()
							.getSystemDate()));
				} else {
					frame.append2Data(sdf.format(sam.getEntryDate()));
				}

				frame.append2Data("|");
				frame.append2Data(origin);
				
				// frame.append2Data("||||N||||1||||||||||O");
				frame.append2Data("||||N||||" + liquidTypeString
						+ "||||||||||O");
				
				//Add Frame
//				frame.append2Data(AstmFrame.CR);
				driver.addFrame(frame);
				mySequence++;
				if(mySequence == 8) mySequence = 1;
				frame = new AstmFrame(driver.getInstrument(), mySequence, AstmFrame.FRAME_TYPE_CONTINUITY);
				//---------

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

				//Add Frame
//				frame.append2Data(AstmFrame.CR);
				driver.addFrame(frame);
				mySequence++;
				if(mySequence == 8) mySequence = 1;
				frame = new AstmFrame(driver.getInstrument(), mySequence, AstmFrame.FRAME_TYPE_CONTINUITY);
				//---------

			}
			frame.append2Data("L|1|N");
			frame.append2Data(AstmFrame.CR);
		}

		//Add Frame
//		addAndSplitDataFrame(driver, frame);
		//---------

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

	
	
	
}
