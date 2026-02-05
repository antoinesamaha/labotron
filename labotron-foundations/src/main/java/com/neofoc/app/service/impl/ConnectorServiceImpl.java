package com.neofoc.app.service.impl;

import com.foc.Globals;
import com.foc.desc.FocConstructor;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.model.dto.SampleFromLisDTO;
import com.neofoc.app.model.dto.TestFromLisDTO;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.FocTestLabelMap;
import com.neofoc.app.service.CommunicationLogService;
import com.neofoc.app.service.ConnectorService;
import com.neofoc.app.service.DispatcherService;
import com.neofoc.app.service.RabbitMQSendingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ConnectorServiceImpl implements ConnectorService {

    private final DispatcherService dispatcher;
    private final RabbitMQSendingService rabbitMQSendingService;
    private final CommunicationLogService communicationLogService;

    ConnectorServiceImpl(DispatcherService dispatcher, RabbitMQSendingService rabbitMQSendingService, CommunicationLogService communicationLogService){
        this.dispatcher = dispatcher;
        this.rabbitMQSendingService = rabbitMQSendingService;
        this.communicationLogService = communicationLogService;
    }

    @Override
    public void processSampleFromLis(SampleFromLisDTO sampleFromLis) {
        // Create a HashMap to group tests by instrument
        HashMap<String, SampleFromLisDTO> instrumentToSampleMap = new HashMap<>();

        List<TestFromLisDTO> testsList = sampleFromLis.getTests();
        if (testsList != null && !testsList.isEmpty()) {
            for (TestFromLisDTO test : testsList) {
                log.info("Processing test: " + test.getTestCode() + " - " + test.getTestDesc());
                FocTestLabelMap testLabelMap = dispatcher.getInstrumentForTest(test.getTestCode(), test.getSuggestedInstrumentCode());

                if (testLabelMap != null) {
                    String instrumentCode = testLabelMap.getInstrument().getCode();
                    test.setInstrumentCode(instrumentCode);

                    // Get or create sample DTO for this instrument
                    SampleFromLisDTO instrumentSample = instrumentToSampleMap.get(instrumentCode);
                    if (instrumentSample == null) {
                        // Create a new sample DTO for this instrument with the same patient/sample info
                        instrumentSample = new SampleFromLisDTO();
                        instrumentSample.setSampleId(sampleFromLis.getSampleId());
                        instrumentSample.setSampleType(sampleFromLis.getSampleType());
                        instrumentSample.setPatientId(sampleFromLis.getPatientId());
                        instrumentSample.setFirstName(sampleFromLis.getFirstName());
                        instrumentSample.setLastName(sampleFromLis.getLastName());
                        instrumentSample.setMiddleInitial(sampleFromLis.getMiddleInitial());
                        instrumentSample.setDateOfBirth(sampleFromLis.getDateOfBirth());
                        instrumentSample.setSex(sampleFromLis.getSex());
                        instrumentSample.setCurrentDateTime(sampleFromLis.getCurrentDateTime());
                        instrumentSample.setCollectionDate(sampleFromLis.getCollectionDate());
                        instrumentSample.setOrigin(sampleFromLis.getOrigin());
                        instrumentSample.setTests(new ArrayList<>());

                        // Add to the map
                        instrumentToSampleMap.put(instrumentCode, instrumentSample);
                    }

                    // Add this test to the instrument's sample
                    instrumentSample.getTests().add(test);
                }
            }
        } else {
            log.info("No tests found in the sample.");
        }

        // Log the results
        log.info("Created {} instrument-specific samples", instrumentToSampleMap.size());
        for (Map.Entry<String, SampleFromLisDTO> entry : instrumentToSampleMap.entrySet()) {
            log.info("Instrument {} has {} tests", entry.getKey(), entry.getValue().getTests().size());
        }

        FocLabSample focLabSample = newAndsaveToDB(instrumentToSampleMap);
        if (focLabSample != null) {
            sendToDrivers(instrumentToSampleMap, focLabSample);
            focLabSample.dispose();
        }
    }

    public void sendToDrivers(HashMap<String, SampleFromLisDTO> instrumentToSampleMap, FocLabSample focLabSample) {
        for (Map.Entry<String, SampleFromLisDTO> entry : instrumentToSampleMap.entrySet()) {
            String instrumentCode = entry.getKey();
            SampleFromLisDTO sampleForInstrument = entry.getValue();

            // Here you would implement the actual sending logic
            log.info("Sending sample ID {} with {} tests to instrument {}", sampleForInstrument.getSampleId(), sampleForInstrument.getTests().size(), instrumentCode);

            try {
                FocDesc focDesc = Globals.getApp().getFocDescByName("instrument");
                FocList list = focDesc.getFocList();
                list.loadIfNotLoadedFromDB();
                FocInstrument instrument = (FocInstrument) list.searchByPropertyStringValue("code", instrumentCode);

                //Only if the driver is not Inquiry based we send to the queue. Because Inquiry means we only send to the instrument upon a request
                //From the Instrument through the socket
                if (!instrument.getDriver().isInquiryBased()) {
                    rabbitMQSendingService.sendToDriver(instrument, instrumentCode, sampleForInstrument.getSampleId(), sampleForInstrument);
                } else {
                    log.info("  Instrument {} is Enquiry based, test will not be sent", instrumentCode);
                }

            } catch (Exception e) {
                log.error("Failed to send to instrument {}: {}", instrumentCode, e.getMessage(), e);
            }
            // For demonstration, we just log the action
            // In a real implementation, you would call the appropriate service/method to send the data
        }
    }

    public FocLabSample newAndsaveToDB(HashMap<String, SampleFromLisDTO> instrumentToSampleMap) {
        FocList instrumentList = FocInstrument.getFocDesc().getFocList();

        FocLabSample labSample = null;//new FocLabSample(new FocConstructor(Globals.getApp().getFocDescByName("lab_sample")));
        // Implement database saving logic here
        for (Map.Entry<String, SampleFromLisDTO> entry : instrumentToSampleMap.entrySet()) {
            String instrumentCode = entry.getKey();
            SampleFromLisDTO sampleForInstrument = entry.getValue();

            if (labSample == null) {
                labSample = FocLabSample.loadForSampleId(sampleForInstrument.getSampleId());
                if (labSample == null) {
                    labSample = new FocLabSample(new FocConstructor(Globals.getApp().getFocDescByName("lab_sample")));
                    labSample.setCreated(true);

                    labSample.setSampleId(sampleForInstrument.getSampleId());
                    labSample.setLiquidTypeFromLIS(sampleForInstrument.getSampleType());
                    //labSample.setLiquidType(sampleForInstrument.getSampleType());
                    labSample.setPatientId(sampleForInstrument.getPatientId());
                    labSample.setFirstName(sampleForInstrument.getFirstName());
                    labSample.setLastName(sampleForInstrument.getLastName());
                    labSample.setMiddleName(sampleForInstrument.getMiddleInitial());
                    labSample.setDateOfBirth(sampleForInstrument.getDateOfBirth());
                    labSample.setSex(sampleForInstrument.getSex());
                    labSample.setEntryDateTime(sampleForInstrument.getCollectionDate());
                    labSample.setOrigin(sampleForInstrument.getOrigin());
                }
            }

            FocList testList = labSample.getTestList();
            for (TestFromLisDTO testFromLis : sampleForInstrument.getTests()) {
                FocLabTest focLabTest = (FocLabTest) testList.searchByPropertyStringValue(FocLabTest.FNAME_LABEL, testFromLis.getTestCode());
                if (focLabTest == null) {
                    focLabTest = (FocLabTest) testList.newEmptyItem();
                    focLabTest.setCreated(true);
                    focLabTest.setLabel(testFromLis.getTestCode());
                    testList.add(focLabTest);
                }

                focLabTest.setDispatchInstrument((FocInstrument) instrumentList.searchByPropertyStringValue("code", instrumentCode));
                //focLabTest.setLabSample(labSample);
                focLabTest.setDescrip(testFromLis.getTestDesc());

                FocInstrument instrument = (FocInstrument) instrumentList.searchByPropertyStringValue("code", testFromLis.getInstrumentCode());
                focLabTest.setDispatchInstrument(instrument);
                focLabTest.setStatus(FocLabTest.TEST_STATUS_AVAILABLE_IN_L3);
                focLabTest.setValue(0);
                focLabTest.setUnitLabel("");
                focLabTest.setNotes("");
            }

            // Here you would implement the actual DB saving logic
            log.info("Saving sample ID {} with {} tests for instrument {} to the database", sampleForInstrument.getSampleId(), sampleForInstrument.getTests().size(), instrumentCode);

            // For demonstration, we just log the action
            // In a real implementation, you would call the appropriate DAO/service method to save the data
        }

        if (labSample != null) {
            labSample.validate(true);
        }

        return labSample;
    }
}