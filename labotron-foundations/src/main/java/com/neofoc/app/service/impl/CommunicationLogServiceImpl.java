package com.neofoc.app.service.impl;

import com.foc.Application;
import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.focObjects.FocCommunicationLog;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.service.CommunicationLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
public class CommunicationLogServiceImpl implements CommunicationLogService {

    public void log(int communicationPoint, String uuid, FocInstrument instrument, String sampleId, String json) {
        Application app = Globals.getApp();
        FocDesc focDesc = app.getFocDescByName("communication_log");

         FocList focList = focDesc.newFocList();

        FocCommunicationLog focCommunicationLog = (FocCommunicationLog) focList.newEmptyItem();
        focCommunicationLog.setDateTime(LocalDateTime.now());

        focCommunicationLog.setUuid(uuid);
        focCommunicationLog.setCommunicationPoint(communicationPoint);
        switch (communicationPoint) {
            case RECEIVED_LIS_2_CONNECTOR:
                focCommunicationLog.setDirection(CommunicationLogServiceImpl.RECEIVING);
                focCommunicationLog.setSender(CommunicationLogServiceImpl.LIS);
                focCommunicationLog.setReceiver(CommunicationLogServiceImpl.CONNECTOR);
                break;
            case SENT_CONNECTOR_2_DRIVER:
                focCommunicationLog.setDirection(CommunicationLogServiceImpl.SENDING);
                focCommunicationLog.setSender(CommunicationLogServiceImpl.CONNECTOR);
                focCommunicationLog.setReceiver(CommunicationLogServiceImpl.DRIVER);
                break;
            case SENT_DRIVER_2_INSTRUMENT:
                focCommunicationLog.setDirection(CommunicationLogServiceImpl.SENDING);
                focCommunicationLog.setSender(CommunicationLogServiceImpl.DRIVER);
                focCommunicationLog.setReceiver(CommunicationLogServiceImpl.INSTRUMENT);
                break;
            case RECEIVED_INSTRUMENT_2_DRIVER:
                focCommunicationLog.setDirection(CommunicationLogServiceImpl.RECEIVING);
                focCommunicationLog.setSender(CommunicationLogServiceImpl.INSTRUMENT);
                focCommunicationLog.setReceiver(CommunicationLogServiceImpl.DRIVER);
                break;
            case SENT_DRIVER_2_CONNECTOR:
                focCommunicationLog.setDirection(CommunicationLogServiceImpl.SENDING);
                focCommunicationLog.setSender(CommunicationLogServiceImpl.DRIVER);
                focCommunicationLog.setReceiver(CommunicationLogServiceImpl.CONNECTOR);
                break;
            case SENT_CONNECTOR_2_LIS:
                focCommunicationLog.setDirection(CommunicationLogServiceImpl.SENDING);
                focCommunicationLog.setSender(CommunicationLogServiceImpl.CONNECTOR);
                focCommunicationLog.setReceiver(CommunicationLogServiceImpl.LIS);
                break;
        }

        focCommunicationLog.setInstrument(instrument);
        focCommunicationLog.setSampleId(sampleId);
        focCommunicationLog.setJsonContent(json);
        focCommunicationLog.setCreated(true);
        focCommunicationLog.save();
        focList.validate(false);
    }

    @Override
    public void logReception(String uuid) {
        StringBuffer buffer = new StringBuffer("UPDATE communication_log set received = 1, reception_date_time = NOW() where uuid = '"+uuid+"'; ");
        Globals.getApp().getDataSource().command_ExecuteRequest(buffer);
    }

}