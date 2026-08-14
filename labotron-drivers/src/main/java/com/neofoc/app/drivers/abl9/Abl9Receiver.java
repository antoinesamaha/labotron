package com.neofoc.app.drivers.abl9;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmReceiver;
import com.neofoc.app.drivers.octa.OctaFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class Abl9Receiver extends AstmReceiver {

    public Abl9Receiver(AstmDriver driver) {
        super(driver);
    }

    public void respondToInquiryIfNecessary(OctaFrame frame) {
        boolean error = false;

        final FocInstrument instrument = driver.getInstrument();
        StringBuffer data = frame.getData();
        // String sampleId = readString(data, INQUIRY__PATIENT_ID);

        // Globals.logString("Calling sendASampleAnsweringInquiry : "+sampleId);
        
        if (instrument != null && !driver.reserve()) {
//            try {
//                AlegriaFrameCreator creator = new AlegriaFrameCreator();
//                driver.getL3SerialPort().send(creator.buildAckFrame());
//
//                //Loading the L3Message to send
//                L3SampleTestJoinFilter filter = instrument.getSampleListToSendAfterEnquiry(sampleId);
//                filter.setActive(true);
//                instrument.logString("Loading FocLabSample to Send : " + sampleId);
//                messageSentAsOrderToInstrument = filter.convertToMessage();
//
//                String orderFrame = creator.buildOrderFrame(instrument, messageSentAsOrderToInstrument);
//                driver.getL3SerialPort().send(orderFrame);
//
//                driver.release();
//            } catch (Exception e) {
//                messageSentAsOrderToInstrument = null;
//                Globals.logString("Exception while answering inquiry");
//                Globals.logException(e);
//            }
        }

    }


    /**
     * Intercepts the SOH block before extractDataFromFrame is called.
     *
     * ABL9 sends one self-contained block:
     *   [SOH]H|...[CR]P|...[CR]...L|...[CR][EOT]
     *
     * Strategy:
     *   1. Detect the SOH at position 0 of the raw frame.
     *   2. Pre-fill the concatenation buffer with the CR-delimited records
     *      (content between SOH and EOT, exclusive).
     *   3. A trailing '|' is appended to each record so that
     *      extractDataFromConcatenatedFrame's substring(1, length-1) strips
     *      that '|' instead of the last data character.
     *   4. Replace the frame's dataWithFrame with a bare EOT so the parent's
     *      EOT branch processes the now-populated concatenation buffer.
     */
    @Override
    protected int concatenateResultFrame(AstmFrame frame) {
        StringBuffer dataWithFrame = frame.getDataWithFrame();

        if (dataWithFrame != null && dataWithFrame.length() > 0
                && dataWithFrame.charAt(0) == AstmFrame.SOH) {

            String raw = dataWithFrame.toString();
            int eotIdx = raw.lastIndexOf(AstmFrame.EOT);
            if (eotIdx > 1) {
                String records = raw.substring(1, eotIdx); // strip SOH, exclude EOT
                // Append trailing | per record so the -1 slice in extractDataFromConcatenatedFrame
                // removes the delimiter rather than a data character.
                records = records.replace(String.valueOf(AstmFrame.CR), "|" + AstmFrame.CR);
                getConcatenationBuffer().append(records);
                Globals.logString("ABL9 SOH block pre-filled into concatenation buffer");
            }

            // Replace with a bare EOT to trigger the parent's EOT processing path.
            frame.setDataWithFrame(new StringBuffer(String.valueOf(AstmFrame.EOT)));
        }

        return super.concatenateResultFrame(frame);
    }
}
