package com.neofoc.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foc.Globals;
import com.foc.list.FocList;
import com.neofoc.app.driver.MessageListener;
import com.neofoc.app.model.dto.SampleResultDTO;
import com.neofoc.app.model.dto.TestResultDTO;
import com.neofoc.app.modules.labotron.LabTest;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;
import com.neofoc.app.utils.SpringContextUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


public class InstrumentReceiverListener implements MessageListener {

    private FocInstrument instrument;

    public InstrumentReceiverListener(FocInstrument instrument) {
        this.instrument = instrument;
    }

    public void dispose(){
        this.instrument = null;
    }

    @Override
    public void messageReceived(L3Message message) {
        try {
            CommunicationLogService communicationLogService = SpringContextUtil.getBean(CommunicationLogService.class);

            if (message == null) {
                Globals.logString("Received null message, ignoring.");
                return;
            }

            // Process the sample results further as needed
            writeReceivedSamplesToDB(message);

            List<SampleResultDTO> allSampleResults = convertL3MessagetoDTOs(message);

            for (SampleResultDTO sampleResult : allSampleResults) {
                // Log this sample result
                String json = convertToJson(sampleResult);
//                communicationLogService.log(
//                        CommunicationLogService.SENT_CONNECTOR_2_LIS,
//                        null,
//                        instrument,
//                        sampleResult.getSampleId(),
//                        json
//                );

                RabbitMQSendingService rmqSendingService = SpringContextUtil.getBean(RabbitMQSendingService.class);
                rmqSendingService.sendToConnector(sampleResult.getSampleId(), json);
            }

        } catch (Exception e) {
            Globals.logException(e);
        }
    }

    /**
     * Converts a test status code to a string representation.
     */
    private String getStatusString(int status) {
        // Map status codes to strings based on your system's definitions
        switch (status) {
            case FocLabTest.TEST_STATUS_ANALYSING:
                return "ANALYSING";
            case FocLabTest.TEST_STATUS_AVAILABLE_IN_L3:
                return "AVAILABLE IN LABOTRON";
            case FocLabTest.TEST_STATUS_SENDING_TO_INSTRUMENT:
                return "SENDING_TO_INSTRUMENT";
            case FocLabTest.TEST_STATUS_COMMITED_TO_LIS:
                return "COMMITED_TO_LIS";
            case FocLabTest.TEST_STATUS_ERROR_WIHLE_COMMIT_TO_LIS:
                return "ERROR_WIHLE_COMMIT_TO_LIS";
            case FocLabTest.TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED:
                return "NOT_IN_LABOTRON_WHEN_RESULT_RECEIVED";
            case FocLabTest.TEST_STATUS_NOT_IN_LIS_WHEN_COMMITING_RESULT:
                return "NOT_IN_LIS_WHEN_COMMITING_RESULT";
            case FocLabTest.TEST_STATUS_RESULT_AVAILABLE:
                return "RESULT_AVAILABLE";
            default:
                return "UNKNOWN";
        }
    }

    /**
     * Converts a SampleResultDTO to JSON string.
     */
    private String convertToJson(SampleResultDTO sampleResult) {
        try {
            // Get ObjectMapper from Spring context or create a new one
            ObjectMapper objectMapper = SpringContextUtil.getBean(ObjectMapper.class);
            if (objectMapper == null) {
                objectMapper = new ObjectMapper();
            }
            return objectMapper.writeValueAsString(sampleResult);
        } catch (Exception e) {
            System.err.println("Error converting to JSON: " + e.getMessage());
            return "{\"error\":\"Failed to convert to JSON\"}";
        }
    }

    /**
     * Process the sample results further as needed.
     */
    private void writeReceivedSamplesToDB(L3Message message) {

        FocList sampleDBList = FocLabSample.getFocDesc().newFocList();

        // Load the sampleDBList by filtering on the sample_id in (...).
        String sampleIdIn = null;
        Iterator<FocLabSample> sampleIterator = message.sampleIterator();
        while (sampleIterator != null && sampleIterator.hasNext()) {
            FocLabSample sample = sampleIterator.next();

            if (sampleIdIn == null) sampleIdIn = "'"+sample.getSampleId()+"'";
            else sampleIdIn += ",'" + sample.getSampleId()+"'";
        }

        sampleDBList.getFilter().putAdditionalWhere("SAMPLE_IDS", "sample_id in (" + sampleIdIn + ")");
        sampleDBList.loadIfNotLoadedFromDB();

        // Now iterate through the message samples and for each sample iterate through its tests.
        sampleIterator = message.sampleIterator();
        while (sampleIterator != null && sampleIterator.hasNext()) {
            FocLabSample sample = sampleIterator.next();

            FocLabSample sampleDB = (FocLabSample) sampleDBList.searchByPropertyStringValue("sample_id", sample.getSampleId());
            if (sampleDB == null) {
                sampleDB = (FocLabSample) sampleDBList.newEmptyItem();
                sampleDB.copyWithoutTests(sample);
                sampleDB.setCreated(true);
            } else {
                sampleDB.getTestList().loadIfNotLoadedFromDB();
            }

            Iterator<FocLabTest> testIterator = sample.testIterator();
            while (testIterator != null && testIterator.hasNext()) {
                FocLabTest test = testIterator.next();

                FocLabTest testDB = (FocLabTest) sampleDB.getTestList().searchByPropertyStringValue("label", test.getLabel());
                if (testDB == null) {
                    testDB = (FocLabTest) sampleDB.getTestList().newEmptyItem();
                    testDB.setCreated(true);
                    testDB.setLabel(test.getLabel());
                    testDB.setStatus(FocLabTest.TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED);
                    test.setStatus(FocLabTest.TEST_STATUS_NOT_IN_L3_WHEN_RESULT_RECEIVED);
                } else {
                    testDB.setStatus(FocLabTest.TEST_STATUS_RESULT_AVAILABLE);
                    test.setStatus(FocLabTest.TEST_STATUS_RESULT_AVAILABLE);
                }
                testDB.setResultOk(test.getResultOk());
                testDB.setValue(test.getValue());
                testDB.setAlarm(test.getAlarm());
                testDB.setActualInstrument(instrument);
                test.setActualInstrument(instrument);
                testDB.setUnitLabel(test.getUnitLabel());
                testDB.setNotificationMessage(test.getNotificationMessage());
            }
        }

        sampleDBList.validate(false);
        sampleDBList.dispose();
    }

    private List<SampleResultDTO> convertL3MessagetoDTOs(L3Message message) {
        // Create a list to hold all sample result DTOs
        List<SampleResultDTO> allSampleResults = new ArrayList<>();

        // Iterate through all samples in the message and fill the DTOs
        Iterator<FocLabSample> sampleIterator = message.sampleIterator();
        while (sampleIterator != null && sampleIterator.hasNext()) {
            FocLabSample sample = sampleIterator.next();
            String sampleId = sample.getSampleId();

            // Create a list to hold all test results for this sample
            List<TestResultDTO> testResults = new ArrayList<>();

            // Iterate through the tests for this sample
            Iterator<FocLabTest> testIterator = sample.testIterator();
            while (testIterator != null && testIterator.hasNext()) {
                FocLabTest test = testIterator.next();

                // Create a TestResultDTO for each test
                TestResultDTO testResult = TestResultDTO.builder()
                        .testId(test.getLabel())
                        .status(getStatusString(test.getStatus()))
                        .actualAnalyzerCode(instrument.getCode())
                        .alarm(test.getAlarm())
                        .result(test.getValue())
                        .notes(test.getValueNotes())
                        .unit(test.getUnitLabel())
                        .message(test.getNotificationMessage())
                        .verificationPending(test.isVerificationPendingFlag())
                        .dateTime(LocalDateTime.now())
                        .build();

                // Add the test result to the list
                testResults.add(testResult);
            }

            // Create a SampleResultDTO with all test results for this sample
            SampleResultDTO sampleResult = SampleResultDTO.builder()
                    .sampleId(sampleId)
                    .tests(testResults)
                    .build();

            // Add the sample result to the list
            allSampleResults.add(sampleResult);
        }

        return allSampleResults;
    }

}
