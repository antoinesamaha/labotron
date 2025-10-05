package com.neofoc.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foc.Globals;
import com.neofoc.app.driver.MessageListener;
import com.neofoc.app.model.dto.SampleResultDTO;
import com.neofoc.app.model.dto.TestResultDTO;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;
import com.neofoc.app.utils.SpringContextUtil;

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

            // Create a list to hold all sample result DTOs
            List<SampleResultDTO> allSampleResults = new ArrayList<>();

            // Iterate through all samples in the message
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
                        .actualAnalyzerCode(message.getInstrumentCode())
                        .alarm(test.getAlarm() == 1 ? Boolean.TRUE : Boolean.FALSE)
                        .result(test.getValue())
                        .notes(test.getValueNotes())
                        .unit(test.getUnitLabel())
                        .message(test.getNotificationMessage())
                        .verificationPending(test.isVerificationPendingFlag())
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

                // Log this sample result
                String json = convertToJson(sampleResult);
                communicationLogService.log(
                        CommunicationLogService.RECEIVED_INSTRUMENT_2_DRIVER,
                        null,
                        instrument,
                        sampleId,
                        json
                );

                RabbitMQSendingService rmqSendingService = SpringContextUtil.getBean(RabbitMQSendingService.class);
                rmqSendingService.sendToLis(json);
            }

            // Process the sample results further as needed
            processSampleResults(allSampleResults);

        } catch (Exception e) {
            // Log the exception
            System.err.println("Error processing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Converts a test status code to a string representation.
     */
    private String getStatusString(int status) {
        // Map status codes to strings based on your system's definitions
        switch (status) {
            case 1:
                return "COMPLETED";
            case 2:
                return "PENDING";
            case 3:
                return "ERROR";
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
    private void processSampleResults(List<SampleResultDTO> sampleResults) {
        // Add your business logic to process the sample results
        // For example, sending them to another service or updating a database
        for (SampleResultDTO sampleResult : sampleResults) {
            Globals.logString("Processed Sample ID: " + sampleResult.getSampleId());

        }
    }

}
