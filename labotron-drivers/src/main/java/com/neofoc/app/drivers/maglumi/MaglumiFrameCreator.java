package com.neofoc.app.drivers.maglumi;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

/**
 * Builds the records as described in the Maglumi "Specification of LIS interface" (B.5, B.6.2):
 *
 *   H|\^&||PSWD|Maglumi 4000 Plus|||||Lis||P|E1394-97|20100319
 *   P|1||||Last^First^M|||F
 *   O|1|1234567||^^^CA125|R
 *   O|2|1234567||^^^CA153|R
 *   L|1|N
 *
 * Only the records are built here (no ENQ / EOT), MaglumiDriver.sendFramesArray() sends them.
 */
public class MaglumiFrameCreator extends AstmFrameCreator {

    private static final int FRAME_SIZE = 240;

    @Override
    public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
        resetSequence();
        FocInstrument instrument = driver.getInstrument();

        driver.addFrame(newHeaderFrame(instrument, getNextSequence()));

        int sampleSequence = 1;
        Iterator sIter = message.sampleIterator();
        while (sIter != null && sIter.hasNext()) {
            FocLabSample sam = (FocLabSample) sIter.next();
            if (sam != null) {
                driver.addFrame(newMaglumiPatientFrame(instrument, sampleSequence, sam));

                int testSequence = 1;
                Iterator tIter = sam.testIterator();
                while (tIter != null && tIter.hasNext()) {
                    FocLabTest test = (FocLabTest) tIter.next();
                    if (test != null) {
                        String instrCode = driver.testMaps_getInstCode(test.getLabel());
                        driver.addFrame(newMaglumiOrderFrame(instrument, testSequence, sam.getSampleId(), instrCode, test.getPriority()));
                        testSequence++;
                    }
                }
                sampleSequence++;
            }
        }

        AstmFrame frame = newLastFrame(instrument, getNextSequence(), 1);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data('N');
        driver.addFrame(frame);
    }

    @Override
    public AstmFrame newHeaderFrame(FocInstrument instrument, int sequence) {
        AstmFrame frame = new AstmFrame(instrument, sequence, AstmFrame.FRAME_TYPE_HEADER);
        frame.append2Data("|\\^&||PSWD|Maglumi 4000 Plus|||||Lis||P|E1394-97|");
        frame.append2Data(new SimpleDateFormat("yyyyMMdd").format(new Date()));
        return frame;
    }

    protected AstmFrame newMaglumiPatientFrame(FocInstrument instrument, int sequenceNum, FocLabSample sam) {
        AstmFrame frame = new AstmFrame(instrument, 0, AstmFrame.FRAME_TYPE_PATIENT);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequenceNum));
        // Field 6: patient name (max 30)
        frame.append2Data("||||");
        StringBuffer name = new StringBuffer();
        if (sam.getLastName() != null) name.append(sam.getLastName());
        name.append(AstmFrame.COMPONENT_DELIMITER);
        if (sam.getFirstName() != null) name.append(sam.getFirstName());
        if (sam.getMiddleName() != null && !sam.getMiddleName().isEmpty()) {
            name.append(AstmFrame.COMPONENT_DELIMITER);
            name.append(sam.getMiddleName());
        }
        frame.append2Data(name.toString(), 30);
        // Field 9: sex M, F, U
        frame.append2Data("|||");
        String sex = sam.getSex();
        if (sex != null && (sex.equals("M") || sex.equals("F"))) {
            frame.append2Data(sex);
        } else {
            frame.append2Data('U');
        }
        return frame;
    }

    protected AstmFrame newMaglumiOrderFrame(FocInstrument instrument, int sequenceNum, String sampleId, String instrCode, String priority) throws Exception {
        AstmFrame frame = new AstmFrame(instrument, 0, AstmFrame.FRAME_TYPE_ORDER);
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(String.valueOf(sequenceNum));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        frame.append2Data(sampleId, 22);
        frame.append2Data("||");
        appendASingleTest(frame, instrCode, getTestCodeLength(instrument));
        frame.append2Data(AstmFrame.FIELD_SEPERATOR);
        // Priority is required: S = emergency, R = routine
        frame.append2Data("S".equals(priority) ? "S" : "R");
        return frame;
    }

    protected void addAndSplitDataFrame(AstmDriver driver, AstmFrame frame) {
        AstmFrame remainingFrame = frame;
        int sequence = 1;
        while (remainingFrame.getData().length() > FRAME_SIZE) {
            AstmFrame splitFrame = new AstmFrame(driver.getInstrument(), sequence++, AstmFrame.FRAME_TYPE_CONTINUITY);
            splitFrame.setIntermediateFrame(true);
            splitFrame.setData(new StringBuffer(remainingFrame.getData().substring(0, FRAME_SIZE)));
            driver.addFrame(splitFrame);
            remainingFrame.getData().replace(0, FRAME_SIZE, "");
        }

        remainingFrame.setSequence(sequence);
        driver.addFrame(remainingFrame);
    }
	
  /*
  public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
    
    // ENQ frame
    // -----------
    AstmFrame frame = newEnquiryFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -----------
    
    // BIG frame
    frame = new AstmFrame(driver.getInstrument(), 1, AstmFrame.FRAME_TYPE_CONTINUITY);
    
    frame.append2Data("H|\\^&|||host^2||||||TSDWN^BATCH");
    frame.append2Data(AstmFrame.CR);
    
    // Patient Frame
    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      L3Sample sam = (L3Sample) sIter.next();
      if (sam != null) {
      	//Patient
      	frame.append2Data("P|1||PatID|||||");
        if(sam.getSexe().compareTo("F") == 0){
        	frame.append2Data("F||||||");
        }else{
        	frame.append2Data("M||||||");
        }
      	if(sam.getAge() > 0 && sam.getAge() < 150){
      		frame.append2Data(sam.getAge()+"^Y");	
      	}else{
      		frame.append2Data("^Y");
      	}
      	//-------
      	frame.append2Data(AstmFrame.CR);

      	String liquidTypeString = "1";
      	switch(sam.getLiquidType()){
      	case L3Sample.LIQUID_TYPE_CSF:
      		liquidTypeString = "3";
      		break;
      	case L3Sample.LIQUID_TYPE_BODY_FLUID:
      		liquidTypeString = "5";
      		break;
      	case L3Sample.LIQUID_TYPE_SERUM:
      		liquidTypeString = "1";
      		break;
      	case L3Sample.LIQUID_TYPE_URIN:
      		liquidTypeString = "2";
      		break;
      	case L3Sample.LIQUID_TYPE_STOOL:
      	case L3Sample.LIQUID_TYPE_OTHERS:
      		liquidTypeString = "5";
      		break;
      	case L3Sample.LIQUID_TYPE_SUPERNATENT:
      		liquidTypeString = "4";
      		break;
      	}
      	
      	//Order
      	frame.append2Data("O|1|"+sam.getId()+"|"+sam.getId()+"^^^^S"+liquidTypeString+"^SC|");
      	
      	boolean isFirst = true;
        Iterator tIter = sam.testIterator(); 
        while(tIter != null && tIter.hasNext()){
          L3Test test = (L3Test) tIter.next();
          String machineTest = test != null ? driver.testMaps_getInstCode(test.getLabel()) : null;
          if(machineTest != null){
            // Order Frame
          	if(!isFirst){
          		frame.append2Data("\\");
          	}
          	frame.append2Data("^^^"+machineTest+"^");
          	isFirst = false;
            // ---------------
            //if (fromDriver == false){
              //Result Frame
              //frame = AstmFrame.newResultFrame(getInstrument(), getNextSequence(), 1, test.getLabel(), test.getUnitLabel(), test.getValue());
              //addFrame(frame);
              //----------------
            //}
          }
        }
        frame.append2Data("|R||");
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        if(sam.getEntryDate().getTime() < 24*60*60*1000){
          Globals.logString("!!!!!!!!!    Entry date < 1 day    !!!!!!!!!!!"+sam.getEntryDate().getTime());
          frame.append2Data(sdf.format(Globals.getApp().getSystemDate()));
        }else{
          frame.append2Data(sdf.format(sam.getEntryDate()));
        }
                
        //frame.append2Data("||||N||||1||||||||||O");
        frame.append2Data("||||N||||"+liquidTypeString+"||||||||||O");
        frame.append2Data(AstmFrame.CR);
        
        frame.append2Data("C|1|L|"+sam.getFirstName()+" "+sam.getLastName()+"^^^^|G");
        //frame.append2Data("C|1|L|C1^C2^C3^C4^C5|G");
        
        frame.append2Data(AstmFrame.CR);
      }
      frame.append2Data("L|1|N");
      frame.append2Data(AstmFrame.CR);
    }
   
    addAndSplitDataFrame(driver, frame);
    
    // Last frame
    //frame = AstmFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
    //addFrame(frame);
    // -------------

    // EOT frame
    frame = newEndOfTransmissionFrame(driver.getInstrument());
    driver.addFrame(frame);
    // -------------
  }
  */
}
