/*
 * Created on Sep 22, 2019
 */
package com.neofoc.app.drivers.vitekbci;


import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

/**
 * @author 01Barmaja
 */
public class VitekBCIReceiver extends AstmReceiver_COPY {//implements L3SerialPortListener {
    protected AstmDriver driver = null;
//	protected L3Message  message = null;
//	protected L3Sample   sample  = null;
//	protected L3Test     test    = null;

    private VitekResultLineReader resultLineReader = null;

    public VitekBCIReceiver(AstmDriver driver) {
        super(driver);
        this.driver = driver;

        message = null;
        sample = null;
        test = null;

        resultLineReader = new VitekResultLineReader();
    }

    public void disposeMessage() {
        if (message != null) {
            message.dispose();
        }
        message = null;
        sample = null;
        test = null;
    }

    public void dispose() {
        driver = null;

        disposeMessage();

        if (resultLineReader != null) {
            resultLineReader.dispose();
            resultLineReader = null;
        }
    }

    public AstmDriver getDriver() {
        return driver;
    }

//	private void sendMessageBackToInstrument() {
//		if(driver != null && message != null) {
//			driver.notifyListeners(message);
//		}
//	}

    private L3Message getMessage() {
        if (message == null) {
            message = new L3Message();
        }
        return message;
    }

    private FocLabSample getSample() {
        return sample;
    }

    private FocLabTest getTest() {
        return test;
    }

    public void addResult(VitekResultLineReader reader) {
        //Make sure we have the sample
        String sampleID = reader.getSampleID();
        if (sampleID != null && !sampleID.trim().equals("")) {

            if (sample == null || !sample.getSampleId().equals(sampleID)) {
                //In this case I need to add or find my sample
                sample = getMessage().findSample(sampleID);
                if (sample == null) {
                    sample = new FocLabSample(sampleID);
                    sample.setFirstName(reader.getFirstName());
                    sample.setLastName(reader.getLastName());
                    //sample.setMiddleInitial(reader.getMidInitial());
                    message.addSample(sample);
                }
            }

            if (sample != null) {
                String testLabel = reader.getTestLabel();
                if (testLabel != null && !testLabel.equals("")) {
                    testLabel = testLabel.trim();

                    test = sample.findTest(testLabel);
                    if (test == null) {
                        test = sample.addTest();
                        test.setLabel(testLabel);
                    }
                    if (test != null) {
                        String valueStr = resultLineReader.getResultValue();
                        try {
                            //Numerical
                            double value = Double.valueOf(valueStr);
                            test.setValue(value);
                        } catch (Exception e) {
                            test.setValue(0);
                            test.setValueNotes(valueStr);
                        }
                        test.setNotificationMessage(resultLineReader.getResultComment());
                        test.setResultOk(true);
                    }
                }
            }
        }
    }

    protected void treatResultFrame(AstmFrame frame) {
        super.treatResultFrame(frame);
        if (frame.getType() == VitekBCIFrame.FRAME_TYPE_RESULT_VITEK) {
            StringBuffer data = frame.getData();
            resultLineReader = new VitekResultLineReader();
            resultLineReader.scanTokens(this, data);
        }
    }

}
