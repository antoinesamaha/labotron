package com.neofoc.app.drivers.octa;

import com.foc.Globals;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public class OctaFrame extends AstmFrame {

    /*
MSG1	Worklist	request	(or	Total	request)
MSG2	Transfer	OK	(ACK)
MSG3	Demographic	data	of	a	single	patient
MSG4	Transfer	error	(NACK)
MSG5	Result	data	of	a	single	patient
MSG6	Query	request	(or	Partial	request	of	a	single	patient	by	patient	ID	number)
MSG7	No	data	(EOT)
     */

    public static final String ACK_FRAME = String.valueOf(STX)+String.valueOf(ACK)+String.valueOf(ETX);
    public static final String NACK_FRAME = String.valueOf(STX)+String.valueOf(NACK)+String.valueOf(ETX);

    public OctaFrame(FocInstrument instrument) {
        super(instrument);
    }

    @Override
    public void createDataWithFrame() throws Exception {

    }

    @Override
    public void extractDataFromFrame() throws Exception {

    }

    @Override
    public boolean extractAnswerFromBuffer(StringBuffer buffer) {
        Globals.logString("OctaDriver extractAnswerFromBuffer");
        String str = buffer.toString();
        boolean extractionDone = false;

        String start = String.valueOf(STX);
        String endEtx = String.valueOf(ETX);
        String endEot = String.valueOf(EOT);

        int sIdx = str.lastIndexOf(start);
        int eIdx = -1;
        if (sIdx >= 0) {
            eIdx = str.lastIndexOf(endEot);
            if (eIdx < 0) {
                eIdx = str.lastIndexOf(endEtx);
            }
        }
        Globals.logString("OctaDriver extractAnswerFromBuffer ["+sIdx+":"+eIdx+"]");
        if (sIdx >= 0 && eIdx > sIdx) {
            StringBuffer response = new StringBuffer(buffer.subSequence(sIdx, eIdx + 1));
            setDataWithFrame(response);
            buffer.replace(0, eIdx + 1, "");
            extractionDone = true;
            Globals.logString("OctaDriver returning True");
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Globals.logException(e);
        }

        return extractionDone;
    }

}
