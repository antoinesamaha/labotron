package com.neofoc.app.drivers.maglumi;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;

public class MaglumiFrameCreator extends AstmFrameCreator {

    private static final int FRAME_SIZE = 240;

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
