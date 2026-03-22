package com.neofoc.app.drivers.vitekbci;

import com.foc.list.FocList;
import com.foc.util.ASCII;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.drivers.astm.AstmFrameCreator;
import com.neofoc.app.modules.labotron.LabSample;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.text.SimpleDateFormat;
import java.util.Iterator;

/**
 *
 * @author Antoine SAMAHA
 * <p>
 * mtmpr|pi324234|pnBrown|pb1996/12/02|psM|soLAB1|si|ci123123|rtHIV|qd1
 *
 */
public class VitekBCIFrameCreator extends AstmFrameCreator {

    public static SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");

    public VitekBCIFrameCreator() {
    }

    public VitekBCIFrame newLastFrame(FocInstrument instrument) {
        VitekBCIFrame frame = new VitekBCIFrame(instrument, AstmFrame.FRAME_TYPE_LAST);
        return frame;
    }

    public VitekBCIFrame newEndOfTransmissionFrame(FocInstrument instrument) {
        VitekBCIFrame frame = new VitekBCIFrame(instrument, AstmFrame.FRAME_TYPE_EOT);
        return frame;
    }

    public VitekBCIFrame newOrderFrame(FocInstrument instrument, FocLabSample sam) {
        VitekBCIFrame frame = new VitekBCIFrame(instrument, AstmFrame.FRAME_TYPE_ORDER);

        StringBuffer buffer = new StringBuffer();

        FocList testList = sam.getTestList();
        for (int t = 0; t < testList.size(); t++) {
            FocLabTest test = (FocLabTest) testList.getFocObject(t);

            //mtmpr|pi34234|pnBrown|pb1996/12/02|psM|soLAB1|si|ci123123|rtHIV|qd1
            buffer.append(ASCII.RS);
            buffer.append("mtmpr");
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("pi");
            buffer.append(sam.getPatientId());
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("pn");
            buffer.append(sam.getFirstName() + " " + sam.getLastName());
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("pb");
            buffer.append(sdf.format(sam.getDateOfBirth()));
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("ps");
            buffer.append(sdf.format(sam.getSex()));
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("so");
            buffer.append("L3");
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("si");
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("ci");
            buffer.append(sam.getId());
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("rt");
            buffer.append(test.getLabel());
            buffer.append(AstmFrame.FIELD_SEPERATOR);

            buffer.append("qd1");
        }

        frame.setData(buffer);

        return frame;
    }

    public void buildFrameArray(AstmDriver driver, L3Message message, boolean fromDriver) throws Exception {
        Iterator sIter = message.sampleIterator();
        while (sIter != null && sIter.hasNext()) {
            FocLabSample sam = (FocLabSample) sIter.next();
            if (sam != null) {
                // ENQ frame
                // -----------
                AstmFrame frame = newEnquiryFrame(driver.getInstrument());
                driver.addFrame(frame);
                // -----------

                frame = newOrderFrame(driver.getInstrument(), sam);
                driver.addFrame(frame);

                // <ETX> frame
                // ----------------
                frame = newLastFrame(driver.getInstrument());
                driver.addFrame(frame);
                // ----------------

                // <EOT> frame
                // ----------------
                frame = newEndOfTransmissionFrame(driver.getInstrument());
                driver.addFrame(frame);
                // ----------------
            }
        }
    }
}
