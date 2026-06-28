package com.neofoc.app.drivers.abl9;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

/**
 * Driver for ABL9 (Radiometer) Blood Gas Analyzer.
 *
 * Protocol: Standard ASTM E1394/E1381 via TCP/IP (LIS connection).
 *
 * Bidirectional inquiry-based mode:
 *   - ABL9 sends a Q record querying for patient/order info by accession number
 *   - Q record format: Q|1|PID{patientId}^ACN{accessionNbr}|||||||||D
 *   - Labotron responds with standard H/P/O/L frames
 *   - ABL9 measures and returns results via H/P/O/R.../C/L
 *
 * ABL9 test codes (ASTM/HL7 names): pH, pCO2, pO2, HCO3-, ABE, SBE, SBC,
 *   Hct, K+, Na+, Ca++, Cl-, Glu, Lac, tHb, sO2
 */
public class Abl9Driver extends AstmDriver {

    public Abl9Driver() {
        super();
        getAstmParams().setResultFrame_ComponentPositionForResultType(-1);
        getAstmParams().setYieldOnEnqCollision(true);
        getAstmParams().setReleaseWhenReceivedENQ(true);
        getAstmParams().setReadComment3(true);
        getAstmParams().setReadResultComment(true);
        getAstmParams().setConcatenatedFrames(false);
        getAstmParams().setSendPatientIdToInstrument(true);
        getAstmParams().setTakeAllFramesFromBufferNotJustTheLast(true);
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setUsePatientIdAsSampleId(true);
    }

    @Override
    public boolean isInquiryBased() {
        return true;
    }

    @Override
    protected void initDriverReceiver() {
        Abl9Receiver receiver = new Abl9Receiver(this);

        receiver.setInformationEnquiryReader(new Abl9InformationInquiryReader());
        receiver.setPatientLineReader(new Abl9PatientLineReader());
        receiver.setOrderLineReader(new Abl9OrderLineReader());
        receiver.setResultLineReader(new Abl9ResultLineReader(this));

        setDriverReceiver(receiver);
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        if (props != null) {
            props.put("tcpip", "1");
        }
        super.init(instrument, props);

        Abl9Frame answerFrame = new Abl9Frame(instrument);
        getL3SerialPort().setAnswerFrame(answerFrame);
    }
}
