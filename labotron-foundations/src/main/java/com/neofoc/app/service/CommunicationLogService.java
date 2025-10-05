package com.neofoc.app.service;

import com.neofoc.app.model.dto.SampleFromLisDTO;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;

public interface CommunicationLogService {

    static final String LIS = "LIS";
    static final String CONNECTOR = "CONNECTOR";
    static final String DRIVER = "DRIVER";
    static final String INSTRUMENT = "INSTRUMENT";

    static final String SENT = "Sent";
    static final String RECEIVED = "Received";

    static final int RECEIVED_LIS_2_CONNECTOR = 1;
    static final int SENT_CONNECTOR_2_DRIVER = 2;
    static final int RECEIVED_CONNECTOR_2_DRIVER = 3;
    static final int SENT_DRIVER_2_INSTRUMENT = 4;
    static final int RECEIVED_INSTRUMENT_2_DRIVER = 5;
    static final int SENT_DRIVER_2_CONNECTOR = 6;
    static final int RECEIVED_DRIVER_2_CONNECTOR = 7;
    static final int SENT_CONNECTOR_2_LIS = 8;

    void log(int communicationPoint, String uuid, FocInstrument instrument, String sampleId, String json);
    void logReception(String uuid);
}
