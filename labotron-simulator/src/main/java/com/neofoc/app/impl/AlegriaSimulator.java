package com.neofoc.app.impl;

import com.neofoc.app.Int2ByteConverter;
import com.neofoc.app.Phase;
import com.neofoc.app.SimSocket;

public class AlegriaSimulator extends AbstractSimulator {

    public void simulate() {
        phase = Phase.OPENING_SOCKET;

        socket = new SimSocket(this, 9000);

        //These 3 calls will run in 3 parallel threads
        socket.open();

//        sendingInquiry();
//        sleep(10000);
        sendingResults();

//        socket.close();
    }

    public void sendingInquiry() {
        String[] inquiryFrames = {
            "1H|\\^&",
            "2Q|1|^5586677||||||||||O|",
            "3L|1|N"};

        sendingFrames(inquiryFrames);
    }

    public void sendingResults() {
//        String[] frames = {
//                "1H|\\^&",
//                "2P|1|SAMPLE01|123123123",
//                "3O|2|SAMPLE01||^^^SS-B|R|||||||||N||||||||||||||F|",
//                "4R|3|^^^SS-B|< 5|U/ml|3.5 to 4.5|||F||admin|20230208093130|20230208162848||",
//                "5L|1|N"
//        };
        String[] frames = {
                "1H|\\^&",
                "2P|1|SAMPLE01|123123123",
                "3O|2|5586677||^^^SS-B|R|||||||||N||||||||||||||F|",
                "4R|3|^^^SS-B|< 5|U/ml|3.5 to 4.5|||F||admin|20230208093130|20230208162848||",
                "5L|1|N"
        };

        sendingFrames(frames);
    }

}
