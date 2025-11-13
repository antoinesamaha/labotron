package com.neofoc.app.drivers.yumizenP8000;

import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.drivers.astm.AstmFrame;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

import java.util.Properties;

public class YumizenP8000Driver extends AstmDriver {

    private static String[] messagesToTransmit = {
            "Leucocytosis",
            "Leucopenia",
            "Lymphocytosis",
            "Lymphopenia",
            "Neutrophilia",
            "Neutropenia",
            "Eosinophilia",
            "Myelemia",
            "Large immature cell",
            "Atipical lymphocyte",
            "Left shift",
            "Erythroblasts",
            "Monocytosis",
            "Basophilia",
            "Pancytopenia",
            "Blasts",
            "Erythrocytosis",
            "Cold agglutinins",
            "Anemia",
            "Macrocytosis",
            "Microcytosis",
            "Anitocytosis",
            "Hypochromia",
            "Polkylocytosis",

            "Thrombocytosis",
            "Thrombocytopenia",
            "Schistocytes",
            "Small Cell",
            "Macroplatelets",
            "Platelet aggregates",
            "Erythroblasts",

            "Erythroblasts and Platelet aggregate",
            "Blast 1",
            "Blast 2",
            "Immature Granulocyt",
            "Immature Monocytes",
            "Immature lymphocytes",

            "IDA",
            "Thalassemia suspicion",
            "Spherocytosis suspicion",
            "Spherocytosis"

    };

    private YumizenP8000Sender sender = null;

    public YumizenP8000Driver() {
        super();
        frameCreator.dispose();
        frameCreator = new YumizenP8000FrameCreator();

        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setSendPatientIdToInstrument(true);
        getAstmParams().setSendProfileInsteadOfTestID(true);
        getAstmParams().setSendPatientAgeAndSex(true);
        getAstmParams().setSendPatientDateOfBirth(true);
        getAstmParams().setReadComment1(true);
        getAstmParams().setSlaveBehaviour(true);
        getAstmParams().setTakeAllFramesFromBufferNotJustTheLast(true);
        getAstmParams().setReleaseWhenReceivedENQ(true);

        sender = new YumizenP8000Sender(this);
    }

    public void dispose() {
        super.dispose();
        if (sender != null) {
            sender.dispose();
            sender = null;
        }
    }

    @Override
    public void init(FocInstrument instrument, Properties props) throws Exception {
        if (props != null) {
            props.put("tcpip", "1");
        }
        super.init(instrument, props);
        if (sender != null) sender.init();
    }

    protected void initDriverReceiver() {
        setDriverReceiver(new YumizenP8000Receiver(this));
    }

    protected void initAnswerFrame() {
        YumizenP8000Frame answerFrame = new YumizenP8000Frame(getInstrument(), 0);
        getL3SerialPort().setAnswerFrame(answerFrame);
    }

    @Override
    public void connect() throws Exception {
        super.connect();
        // The Sender is transient, should connect and disconnect for every transmission
        // if(sender != null) sender.connect();
    }

    @Override
    public void disconnect() {
        super.disconnect();
        // The Sender is transient, should connect and disconnect for every transmission
        // if(sender != null) sender.disconnect();
    }

    public void sendFramesArray(boolean createDataWithFrame) throws Exception {
        getInstrument().logString("Yumizen sending frames");
        StringBuffer messageToSend = new StringBuffer();
        messageToSend.append(AstmFrame.VT);
        for (int i = 0; i < getFrameCount(); i++) {
            YumizenP8000Frame frame = (YumizenP8000Frame) getFrameAt(i);
            if (frame != null && frame.getData() != null) {
                messageToSend.append(frame.getData());
                messageToSend.append(AstmFrame.CR);
                // getInstrument().logString(frame.getDataWithFrame());
            }
        }
        messageToSend.append(AstmFrame.FS);
        messageToSend.append(AstmFrame.CR);
        messageToSend.append(AstmFrame.LF);

        //ATTENTION
		/*
		messageToSend = new StringBuffer();
		messageToSend.append(AstmFrame.VT);
		messageToSend.append("MSH|^~\\&|LIS|LIS|P8000|P8000|20190703100011||OML^O33^OML_O33|18698910009|P|2.5||||||||");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("PID|||TEST0006ORDER^^^LIS^PI||PAT 1907030001^PAT 1907030001^^||20080302|M|||Main Street^^Springfield^NY^65466^USA^ATC1||||||||||||||||||||N|AL");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("PV1||N|WARD00001^^||||ATD^||||||||||ADD^||ABC123^LIS|||||||||||||||||||||||||20190703100011|20190703100011");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("SPM|1|TEST0006ORDER||EDTA||||MAIN LAB|||||||||20190703100011|20190703100011|||||");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("ORC|NW|TEST0006ORDER|TEST0006ORDER|TEST0006ORDER|||||20190703100011||||||||||||");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("TQ1|||||||20190703100011||R");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("OBR|1|TEST0006ORDER|TEST0006ORDER|CBC^CBC profile^P8000|||||||EDTA||||||||||||||P");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("ORC|NW|TEST0006ORDER|TEST0006ORDER|TEST0006ORDER|||||20190703100011||||||||||||");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("TQ1|||||||20190703100011||R");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("OBR|1|TEST0006ORDER|TEST0006ORDER|DIF^DIF profile^P8000|||||||EDTA||||||||||||||P");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append(AstmFrame.FS);
		messageToSend.append(AstmFrame.CR);
		*/

        boolean error = sender.sendMessage(messageToSend.toString());
        if (!error) {
            getInstrument().logString("Yumizen sending successful");
        } else {
            getInstrument().logString("Yumizen sending returned error");
        }
    }
	
	/*
		//ATTENTION
		messageToSend = new StringBuffer();
		messageToSend.append(AstmFrame.VT);
		messageToSend.append("MSH|^~\\&|LIS|LIS|P8000|P8000|20190703100011||OML^O33^OML_O33|18698910009|P|2.5||||||||");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("PID|||TEST0006ORDER^^^LIS^PI||PAT 1907030001^PAT 1907030001^^||20080302|M|||Main Street^^Springfield^NY^65466^USA^ATC1||||||||||||||||||||N|AL");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("PV1||N|WARD00001^^||||ATD^||||||||||ADD^||ABC123^LIS|||||||||||||||||||||||||20190703100011|20190703100011");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("SPM|1|TEST0006ORDER||EDTA||||MAIN LAB|||||||||20190703100011|20190703100011|||||");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("ORC|NW|TEST0006ORDER|TEST0006ORDER|TEST0006ORDER|||||20190703100011||||W00001||||||||WARD00001^^^^^^^^^W00001");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("TQ1|||||||20190703100011||R");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("OBR|1|TEST0006ORDER|TEST0006ORDER|CBC^CBC profile^P8000|||||||EDTA||||||0033412364566||||||||P");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("ORC|NW|TEST0006ORDER|TEST0006ORDER|TEST0006ORDER|||||20190703100011||||W00001||||||||WARD00001^^^^^^^^^W00001");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("TQ1|||||||20190703100011||R");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append("OBR|1|TEST0006ORDER|TEST0006ORDER|DIF^DIF profile^P8000|||||||EDTA||||||0033412364566||||||||P");
		messageToSend.append(AstmFrame.CR);
		messageToSend.append(AstmFrame.FS);
		messageToSend.append(AstmFrame.CR);
	 */
}
