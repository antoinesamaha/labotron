package com.neofoc.app.drivers.alegria;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.*;
import com.neofoc.app.drivers.octa.OctaFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class AlegriaDriver extends AstmDriver {

    public AlegriaDriver(){
        super();
        if(frameCreator != null){
            frameCreator.dispose();
            frameCreator = null;
        }
        frameCreator = new AlegriaFrameCreator();

        getAstmParams().setResultFrame_ComponentPositionForResultType(-7);
        getAstmParams().setTestCodeLength(3);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setConcatenatedFrames(true);
        getAstmParams().setReadComment3(true);
        getAstmParams().setReadResultComment(true);
        getAstmParams().setTreatHigherLessAlarmSeparately(false);
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        Globals.logString("Init AlegriaDriver");
        if(props != null){
            Globals.logString("AlegriaDriver Set to TCPIP");
            props.put("tcpip", "1");
        }
        super.init(instrument, props);

        AlegriaFrame answerFrame = new AlegriaFrame(instrument);
        getL3SerialPort().setAnswerFrame(answerFrame);
    }

    public boolean isInquiryBased() {
        return true;
    }

    protected void initDriverReceiver() {
        Globals.logString("AlegriaDriver initDriverReceiver");
        super.initDriverReceiver();
        AstmReceiver receiver = (AstmReceiver) getDriverReceiver();
        InformationInquiryReader informationEnquiryReader = receiver.getInformationEnquiryReader();
        informationEnquiryReader.setCMP_SAMPLE_ID(1);
        informationEnquiryReader.setCMP_TUBE_POS(-1);

        PatientLineReader patientLineReader = receiver.getPatientLineReader();
        patientLineReader.setPOS_PATIENT_NAME(2);
        patientLineReader.setPOS_PATIENT_ID(0);

        //setDriverReceiver(new AlegriaReceiver(this));
    }
}

