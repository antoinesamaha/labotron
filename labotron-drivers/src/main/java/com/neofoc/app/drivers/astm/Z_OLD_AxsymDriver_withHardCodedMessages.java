package com.neofoc.app.drivers.astm;
/*package b01.l3.drivers.axsym;

import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import b01.l3.DriverSerialPort;
import b01.l3.Instrument;
import b01.l3.L3Frame;
import b01.l3.data.L3Message;
import b01.l3.data.FocLabSample;
import b01.l3.data.FocLabTest;
import b01.l3.exceptions.L3AnalyzerErrorReturnException;
import b01.l3.exceptions.L3UnexpectedFrameTypeException;

public class Z_OLD_AxsymDriver_withHardCodedMessages extends DriverSerialPort {
  private int frameSequence = 0;
  public static int RESPONSE_TIMEOUT = 30000;
  public static int MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS = 3;
  
//  private String[] arrayLISTestLabels = null;
//  private HashMap<String, Integer> mapLISLabels2Index = null;
  
  public Z_OLD_AxsymDriver_withHardCodedMessages() {
  	super();  	
  }

	
	 * (non-Javadoc)
	 * 
	 * @see b01.l3.Driver#init(java.util.Properties)
	 
	public void init(Instrument instrument, Properties props) throws Exception {
		setDriverReceiver(new AxsymReceiver(this));
		super.init(instrument, props);

		if (!PhMaCh.isPhysicalMachine()) {
			getInstrument().logString("L3 date:" + (new Date(System.currentTimeMillis())).toString());
			newVirtualSerialPort(props);
		} else {
			getInstrument().logString("L3 date : " + (new Date(System.currentTimeMillis())).toString());
			newSerialPort(props);
		}
		AxsymFrame answerFrame = new AxsymFrame(getInstrument());
		getL3SerialPort().setAnswerFrame(answerFrame);

		
		String[] arrayVitrosTestStdLabels = newArrayOfVitrosTestLabels();
		arrayLISTestLabels = new String[arrayVitrosTestStdLabels.length];
		for (int i = 0; i < arrayVitrosTestStdLabels.length; i++) {
			String test = arrayVitrosTestStdLabels[i];
			if (test != null) {
				String testLISLabel = props.getProperty(test);
				if (testLISLabel == null || testLISLabel.trim().compareTo("") == 0) {
					testLISLabel = test;
					arrayLISTestLabels[i] = arrayVitrosTestStdLabels[i];
				}
				mapLISLabels2Index.put(testLISLabel, Integer.valueOf(i));
				arrayLISTestLabels[i] = testLISLabel;
			}
		}
		
	}
  
  @Override
  protected void synchronizeSequenceID() throws Exception {

  }

  private void resetSequence(){
    frameSequence = -1;
  }
  
  private int getNextSequence(){
    frameSequence++;
    if(frameSequence == 8) frameSequence = 0;
    return frameSequence;
  }
  
  public void buildFrameArray(L3Message message) throws Exception {
    resetSequence();
    
    // ENQ frame
    // -----------
    getNextSequence();
    AxsymFrame frame = AxsymFrame.newEnquiryFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // Header frame
    frame = AxsymFrame.newHeaderFrame(getInstrument(), getNextSequence());
    addFrame(frame);
    // -------------
        
    // Patient Frame
    int sampleSequence = 1;

    Iterator sIter = message.sampleIterator();
    while (sIter != null && sIter.hasNext()) {
      FocLabSample sam = (FocLabSample) sIter.next();
      if (sam != null) {
        
        frame = AxsymFrame.newPatientFrame(getInstrument(), getNextSequence(), sampleSequence, sam.getId(), sam.getFirstName(), sam.getLastName(), sam.getMiddleInitial() "X");
        addFrame(frame);
       
        int testSequence = 1;
        Iterator tIter = sam.testIterator(); 
        while(tIter != null && tIter.hasNext()){
          FocLabTest test = (FocLabTest) tIter.next();
          if(test != null){
            // Order Frame
            frame = AxsymFrame.newOrderFrame(getInstrument(), getNextSequence(), testSequence, sam.getId(), test.getLabel());
            addFrame(frame);
            // ---------------
          }
          testSequence++;
        }
        sampleSequence++;
      }
    }
    // Last frame
    frame = AxsymFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
    addFrame(frame);
    // -------------
        
    // EOT frame
    frame = AxsymFrame.newEndOfTransmissionFrame(getInstrument());
    addFrame(frame);
    // -------------
  }

  public void buildFrameArray_EN_DURE_MAUVAISE(L3Message message) throws Exception {
    resetSequence();
    
    
    1H|\^&|||AxSYM^5.00^5455^H1P1O1R1C1Q1L1M1|||||||P|1|20070420165018
    2P|1||||ISSA
    3O|1||700-JAMES^A^02|^^^140^HAVABM_2^UNDILUTED|R||||||||||||||||||||F
    4R|1|^^^140^HAVABM_2^UNDILUTED^^F|0.13|Index||||R||ADMIN||20070420123410
    5R|2|^^^140^HAVABM_2^UNDILUTED^^P|19.41|Rate||||R||ADMIN||20070420123410
    6R|3|^^^140^HAVABM_2^UNDILUTED^^I|NONREACTIVE|||||R||ADMIN||20070420123410
    7L|1
    

    // ENQ frame
    // -----------
    getNextSequence();
    AxsymFrame frame = AxsymFrame.newEnquiryFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // Header frame
    frame = AxsymFrame.newHeaderFrame(getInstrument(), getNextSequence());
    frame.setData(new StringBuffer("|\\^&|||AxSYM^5.00^5455^H1P1O1R1C1Q1L1M1|||||||P|1|20070420165018"));
  //frame.setData(new StringBuffer("|\\^&||||||||||P|1"));
    addFrame(frame);
    // -------------

    frame = AxsymFrame.newPatientFrame(getInstrument(), getNextSequence(), 1, "", "", "", "");
    frame.setData(new StringBuffer("|1||||ISSA"));
  //frame.setData(new StringBuffer("|1|PracPID11|GoodOrder11|A2.ord_PID1|DOE^John1^Q"));    
    addFrame(frame);

    frame = AxsymFrame.newOrderFrame(getInstrument(), getNextSequence(), 1, "BARMAJA", "");
    frame.setData(new StringBuffer("|1|1134567|999-JAMES^A^02|^^^140^HAVABM_2^UNDILUTED|||||||N||||||||||||||O"));
    //frame.setData(new StringBuffer("|1|1134567||^^^106|||||||A||||||||||||||O"));
    addFrame(frame);
    
    // Last frame
    frame = AxsymFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
    addFrame(frame);
    // -------------
        
    // EOT frame
    frame = AxsymFrame.newEndOfTransmissionFrame(getInstrument());
    addFrame(frame);
    // -------------
  }

  public void buildFrameArray_EN_DURE_SIMUL(L3Message message) throws Exception {
    resetSequence();
    
    // ENQ frame
    // -----------
    getNextSequence();
    AxsymFrame frame = AxsymFrame.newEnquiryFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // Header frame
    frame = AxsymFrame.newHeaderFrame(getInstrument(), getNextSequence());
    frame.setData(new StringBuffer("|\\^&||||||||||P|1"));
    addFrame(frame);
    // -------------

    frame = AxsymFrame.newPatientFrame(getInstrument(), getNextSequence(), 1, "", "", "", "");
    frame.setData(new StringBuffer("|1|PracPID11|GoodOrder11|A2.ord_PID1|DOE^John1^Q"));
    addFrame(frame);

    frame = AxsymFrame.newOrderFrame(getInstrument(), getNextSequence(), 1, "BARMAJA", "");
    frame.setData(new StringBuffer("|1|1134567||^^^106|||||||A||||||||||||||O"));
    addFrame(frame);
    
    // Last frame
    frame = AxsymFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
    addFrame(frame);
    // -------------
        
    // EOT frame
    frame = AxsymFrame.newEndOfTransmissionFrame(getInstrument());
    addFrame(frame);
    // -------------    
  }
  
  public void buildFrameArray_EN_DURE_DRIVER_MAUVAISE(L3Message message) throws Exception {
    resetSequence();
    
    // ENQ frame
    // -----------
    getNextSequence();
    AxsymFrame frame = AxsymFrame.newEnquiryFrame(getInstrument());
    addFrame(frame);
    // -----------
    
    // Header frame
    frame = AxsymFrame.newHeaderFrame(getInstrument(), getNextSequence());
    //frame.setData(new StringBuffer("|\\^&|||||||||||P|1"));
    frame.setData(new StringBuffer("|\\^&||||||||||P|1"));
    addFrame(frame);
    // -------------

    frame = AxsymFrame.newPatientFrame(getInstrument(), getNextSequence(), 1, "", "", "", "");
    frame.setData(new StringBuffer("|1"));
    //frame.setData(new StringBuffer("|1|PracPID11|GoodOrder11|A2.ord_PID1|DOE^John1^Q"));    
    addFrame(frame);

    frame = AxsymFrame.newOrderFrame(getInstrument(), getNextSequence(), 1, "BARMAJA", "");
    frame.setData(new StringBuffer("|1|1005||^^^106|||||||N||||||||||||||O"));
    //frame.setData(new StringBuffer("|1|1134567||^^^106|||||||A||||||||||||||O"));
    addFrame(frame);
    
    // Last frame
    frame = AxsymFrame.newLastFrame(getInstrument(), getNextSequence(), 1);
    addFrame(frame);
    // -------------
        
    // EOT frame
    frame = AxsymFrame.newEndOfTransmissionFrame(getInstrument());
    addFrame(frame);
    // -------------
  }
  
  public void send(L3Message message) throws Exception {  	
    //getInstrument().logString("Inst code : " + message.sampleIterator());
    //resetFrameArray();
    //buildFrameArray(message, true);
    //sendFramesArray(true);
  }

  public void sendFramesArray(boolean createDataWithFrame) throws Exception {
    if (createDataWithFrame) {
      for (int i = 0; i < getFrameCount(); i++) {
        L3Frame frame = getFrameAt(i);
        if (frame != null) {
          frame.createDataWithFrame();
          getInstrument().logString(frame.getDataWithFrame());
        }
      }
    }

    int i = 0;
    int numberOfFailures = 0;
    for (i = 0; i < getFrameCount() && numberOfFailures < MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS; i++) {
      AxsymFrame frame = (AxsymFrame) getFrameAt(i);
      if (frame != null) {
        String strToSend = frame.getDataWithFrame().toString();
        AxsymFrame resFrame = null;
        if(frame.getType() == AxsymFrame.FRAME_TYPE_EOT){
        	getL3SerialPort().send(strToSend);
        }else{
        	resFrame = (AxsymFrame)getL3SerialPort().sendAndWaitForResponse(strToSend, RESPONSE_TIMEOUT);
          resFrame.extractDataFromFrame();
          char type = resFrame.getType();
          if (type == AxsymFrame.FRAME_TYPE_ACK) {
          	numberOfFailures = 0;
          } else if (type == AxsymFrame.FRAME_TYPE_NACK) {
          	numberOfFailures++;
            i--;
          } else {
            throw new L3UnexpectedFrameTypeException("Unexpected answer frame type; " + resFrame.getDataWithFrame()+ "\n For request " + frame.getDataWithFrame());
          }
        }
      }
    }
    
    if(numberOfFailures == MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS){
    	throw new L3AnalyzerErrorReturnException("Frame rejected "+ MAX_NUMBER_OF_FRAME_SENDING_ATTEMPTS +" times :" + getFrameAt(i).getDataWithFrame());
    }
  }
  
  public void sendYesFrame(int sequence, boolean isYesToStart) throws Exception {
    AxsymFrame yesFrame = null;
    if (isYesToStart) {
      yesFrame = AxsymFrame.newEnquiryFrame(getInstrument());
    } else {
      yesFrame = AxsymFrame.newHeaderFrame(getInstrument(), sequence);
    }
    yesFrame.createDataWithFrame();
    getL3SerialPort().send(yesFrame.getDataWithFrame().toString());
  }
}


*/