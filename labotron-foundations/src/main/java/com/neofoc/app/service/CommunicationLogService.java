package com.neofoc.app.service;

import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public interface CommunicationLogService {

    static final String LIS = "LIS";
    static final String CONNECTOR = "CONNECTOR";
    static final String DRIVER = "DRIVER";
    static final String INSTRUMENT = "INSTRUMENT";

    static final String SENDING = "Sending";
    static final String RECEIVING = "Receiving";

    static final int RECEIVED_LIS_2_CONNECTOR = 1;
    static final int SENT_CONNECTOR_2_DRIVER = 2;
    static final int SENT_DRIVER_2_INSTRUMENT = 3;
    static final int RECEIVED_INSTRUMENT_2_DRIVER = 4;
    static final int SENT_DRIVER_2_CONNECTOR = 5;
    static final int SENT_CONNECTOR_2_LIS = 6;

    void log(int communicationPoint, String uuid, FocInstrument instrument, String sampleId, String json);
    void logReception(String uuid);
}
