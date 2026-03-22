package com.neofoc.app.drivers.pentraML;

import com.foc.list.FocList;
import com.neofoc.app.drivers.astm.AstmDriver;
import com.neofoc.app.modules.labotron.focObjects.FocLabMessage;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;

import java.util.ArrayList;
import java.util.Iterator;

public class PentraMLDriver extends AstmDriver {

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

    public PentraMLDriver() {
        super();
        frameCreator.dispose();
        frameCreator = new PentraMLFrameCreator();
        //getAstmParams().setPhysicalMachineInfo(new b01.l3.drivers.abbott.axsym.PhMaInfo());
        getAstmParams().setCheckResultFrameTestCodeWithOrderFrameTestCode(false);
        getAstmParams().setSendPatientIdToInstrument(true);
        getAstmParams().setSendProfileInsteadOfTestID(true);
        getAstmParams().setSendPatientAgeAndSex(true);
        getAstmParams().setSendPatientDateOfBirth(true);
        getAstmParams().setReadComment1(true);
        getAstmParams().setSlaveBehaviour(true);
        getAstmParams().setTakeAllFramesFromBufferNotJustTheLast(true);
        getAstmParams().setReleaseWhenReceivedENQ(true);
//  		getAstmParams().setAcceptToStartAtFrame1Directly(true);
    }

    private boolean transmitMessage(String message) {
        boolean include = false;
        for (int i = 0; i < messagesToTransmit.length && !include; i++) {
            include = message.startsWith(messagesToTransmit[i]);
        }
        return include;
    }

    @Override
    public void makeSpecialCommentTreatmentBeforeSendingToLIS(L3Message message) {
        Iterator sampleIterator = message.sampleIterator();
        while (sampleIterator != null && sampleIterator.hasNext()) {
            FocLabSample sample = (FocLabSample) sampleIterator.next();

            FocList messageList = sample.getInstrumentMessageListWithoutLoad();
            if (messageList != null && messageList.size() > 0) {
                ArrayList<FocLabMessage> instMessageToDelete = new ArrayList<FocLabMessage>();
                for (int i = 0; i < messageList.size(); i++) {
                    FocLabMessage instrumentMessage = (FocLabMessage) messageList.getFocObject(i);
                    if (instrumentMessage.getMessage().startsWith("Verification pending")) {
                        FocList testList = sample.getTestList();
                        Iterator iter = testList.focObjectIterator();
                        while (iter != null && iter.hasNext()) {
                            FocLabTest memoryTest = (FocLabTest) iter.next();
                            memoryTest.setVerificationPendingFlag(true);
                        }

                        instMessageToDelete.add(instrumentMessage);
                    } else if (instrumentMessage.getMessage().startsWith("ML DISABLED")) {
                        instMessageToDelete.add(instrumentMessage);
                    } else if (transmitMessage(instrumentMessage.getMessage())) {
                        //In this case we do not delete the message
                        //instMessageToDelete.add(instrumentMessage);
                    } else {//Because we have a lot of messages that the lab does not realy need
                        instMessageToDelete.add(instrumentMessage);
                    }
                }
                for (int i = 0; i < instMessageToDelete.size(); i++) {
                    FocLabMessage instrumentMessage = (FocLabMessage) instMessageToDelete.get(i);
                    messageList.remove(instrumentMessage);
                }
            }
        }
    }
}
