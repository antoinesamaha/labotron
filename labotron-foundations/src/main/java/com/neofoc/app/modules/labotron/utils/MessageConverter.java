package com.neofoc.app.modules.labotron.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foc.Globals;
import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.modules.labotron.focObjects.FocLabTest;
import com.neofoc.app.modules.labotron.focObjects.L3Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class MessageConverter {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Converts a JSON string message to an L3Message object
     * 
     * @param jsonMessage The JSON message to be converted
     * @return L3Message object populated with the data from the JSON
     * @throws IOException If there's an error parsing the JSON
     */
    public static L3Message convertJsonToL3Message(String jsonMessage) throws IOException {
        try {
            // Parse JSON
            JsonNode rootNode = objectMapper.readTree(jsonMessage);
            
            // Create L3Message
            L3Message l3Message = new L3Message();

            // Create and add sample
            String sampleId = rootNode.has("P_SAMPLE_ID") ? rootNode.get("P_SAMPLE_ID").asText() : "";
            FocLabSample sample = new FocLabSample(sampleId);
            l3Message.addSample(sample);

            // Set sample type
            if (rootNode.has("SAMPLE_TYPE")) {
                String sampleType = rootNode.get("SAMPLE_TYPE").asText();
                int liquidType = mapSampleTypeToLiquidType(sampleType);
                sample.setPropertyMultiChoice("liquid_type", liquidType);
            }

            // Set patient information
            if (rootNode.has("PATIENT_ID")) {
                sample.setPropertyString("patient_id", rootNode.get("PATIENT_ID").asText());
            }

            if (rootNode.has("FIRST_NAME")) {
                sample.setPropertyString("first_name", rootNode.get("FIRST_NAME").asText());
            }

            if (rootNode.has("LAST_NAME")) {
                sample.setPropertyString("last_name", rootNode.get("LAST_NAME").asText());
            }

            if (rootNode.has("MIDDLE_INITIAL")) {
                sample.setPropertyString("middle_name", rootNode.get("MIDDLE_INITIAL").asText());
            }

            // Set dates
            if (rootNode.has("DATE_OF_BIRTH")) {
                String dobString = null;
                try {
                    dobString = rootNode.get("DATE_OF_BIRTH").asText();
                    Date dob = parseDate(dobString);
                    sample.setPropertyDate("date_of_birth", new java.sql.Date(dob.getTime()));
                } catch (ParseException e) {
                    if (dobString != null) {
                        Globals.logString("Could not parse DATE_OF_BIRTH " + dobString);
                    } else {
                        Globals.logString("Could not parse DATE_OF_BIRTH Null");
                    }
                }
            }

            if (rootNode.has("CURRENT_DATE_TIME")) {
                String dateTimeString = null;
                try {
                    dateTimeString = rootNode.get("CURRENT_DATE_TIME").asText();
                    Date currentDate = parseDateWithTime(dateTimeString);
                    sample.setPropertyDate("entry_date", new java.sql.Date(currentDate.getTime()));
                } catch (ParseException e) {
                    if (dateTimeString != null) {
                        Globals.logString("Could not parse CURRENT_DATE_TIME " + dateTimeString);
                    } else {
                        Globals.logString("Could not parse CURRENT_DATE_TIME Null");
                    }
                }
            }

            if (rootNode.has("COLLECTION_DATE")) {
                String collectionDateString = null;
                try {
                    collectionDateString = rootNode.get("COLLECTION_DATE").asText();
                    Date collectionDate = parseDate(collectionDateString);
                    sample.setPropertyDate("collection_date", new java.sql.Date(collectionDate.getTime()));
                } catch (ParseException e) {
                    if (collectionDateString != null) {
                        Globals.logString("Could not parse COLLECTION_DATE " + collectionDateString);
                    } else {
                        Globals.logString("Could not parse COLLECTION_DATE Null");
                    }
                }
            }

            // Set gender/sex
            if (rootNode.has("GENDER")) {
                String sex = rootNode.get("GENDER").asText();
                sample.setPropertyString("sex", sex);
            }

            // Set origin
            if (rootNode.has("ORIGIN")) {
                sample.setPropertyString("origin", rootNode.get("ORIGIN").asText());
            }

            // Add the test to the sample
            FocList testList = sample.getTestList();

            // Add tests
            if (rootNode.has("LISTEST") && rootNode.get("LISTEST").isArray()) {
                for (JsonNode testNode : rootNode.get("LISTEST")) {
                    String testCode = null;
                    if (testNode.has("TEST_CODE")) {
                        testCode = testNode.get("TEST_CODE").asText();
                    }
                    FocLabTest test = new FocLabTest(testCode);

                    if (testNode.has("TEST_DESC")) {
                        test.setPropertyString("test_label", testNode.get("TEST_DESC").asText());
                    }
                    
                    // Add the test to the sample
                    testList.add(test);
                }
            }

            return l3Message;
            
        } catch (Exception e) {
            throw new IOException("Error parsing JSON message: " + e.getMessage(), e);
        }
    }
    
    /**
     * Maps the string sample type to the numeric liquid type constants in FocLabSample
     */
    private static int mapSampleTypeToLiquidType(String sampleType) {
        if (sampleType == null || sampleType.trim().isEmpty()) {
            return FocLabSample.LIQUID_TYPE_EMPTY;
        }
        
        switch (sampleType.toLowerCase()) {
            case "serum":
                return FocLabSample.LIQUID_TYPE_SERUM;
            case "urin":
            case "urine":
                return FocLabSample.LIQUID_TYPE_URIN;
            case "csf":
                return FocLabSample.LIQUID_TYPE_CSF;
            case "body fluid":
                return FocLabSample.LIQUID_TYPE_BODY_FLUID;
            case "stool":
                return FocLabSample.LIQUID_TYPE_STOOL;
            case "supernatent":
            case "suprnt":
                return FocLabSample.LIQUID_TYPE_SUPERNATENT;
            default:
                return FocLabSample.LIQUID_TYPE_OTHERS;
        }
    }
    
    /**
     * Parse date string in format DD/MM/YYYY, falling back to ISO (yyyy-MM-dd)
     */
    private static Date parseDate(String dateString) throws ParseException {
        try {
            return new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
        } catch (ParseException e) {
            return new SimpleDateFormat("yyyy-MM-dd").parse(dateString);
        }
    }

    /**
     * Parse date and time string in format DD/MM/YYYY HH:mm:ss, falling back to ISO (yyyy-MM-dd'T'HH:mm:ss)
     */
    private static Date parseDateWithTime(String dateTimeString) throws ParseException {
        try {
            return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").parse(dateTimeString);
        } catch (ParseException e) {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse(dateTimeString);
        }
    }

    /**
     * Converts an L3Message object to a JSON string
     *
     * @param l3Message The L3Message object to be converted
     * @return JSON string representation of the L3Message
     * @throws IOException If there's an error creating the JSON
     */
    public static String convertL3MessageToJson(L3Message l3Message) throws IOException {
        try {
            if (l3Message == null) {
                return "{}";
            }

            StringBuilder jsonBuilder = new StringBuilder();
            jsonBuilder.append("{");

            // Add instrument code if present
            if (l3Message.getInstrumentCode() != null) {
                jsonBuilder.append("\"instrumentCode\":\"").append(l3Message.getInstrumentCode()).append("\",");
            }

            jsonBuilder.append("\"samples\":[");

            // Iterate through samples
            boolean firstSample = true;
            if (l3Message.sampleIterator() != null) {
                var iterator = l3Message.sampleIterator();
                while (iterator.hasNext()) {
                    FocLabSample sample = iterator.next();

                    if (!firstSample) {
                        jsonBuilder.append(",");
                    }
                    firstSample = false;

                    jsonBuilder.append("{");
                    jsonBuilder.append("\"sampleId\":\"").append(sample.getSampleId() != null ? sample.getSampleId() : "").append("\",");
                    jsonBuilder.append("\"patientId\":\"").append(sample.getPropertyString("patient_id") != null ? sample.getPropertyString("patient_id") : "").append("\",");
                    jsonBuilder.append("\"firstName\":\"").append(sample.getPropertyString("first_name") != null ? sample.getPropertyString("first_name") : "").append("\",");
                    jsonBuilder.append("\"lastName\":\"").append(sample.getPropertyString("last_name") != null ? sample.getPropertyString("last_name") : "").append("\",");
                    jsonBuilder.append("\"middleInitial\":\"").append(sample.getPropertyString("middle_name") != null ? sample.getPropertyString("middle_name") : "").append("\",");
                    jsonBuilder.append("\"sex\":\"").append(sample.getPropertyString("sex") != null ? sample.getPropertyString("sex") : "").append("\",");
                    jsonBuilder.append("\"origin\":\"").append(sample.getPropertyString("origin") != null ? sample.getPropertyString("origin") : "").append("\"");

                    // Add tests array
                    jsonBuilder.append(",\"tests\":[");
                    FocList testList = sample.getPropertyList("lab_test_LIST");
                    if (testList != null) {
                        boolean firstTest = true;
                        for (int i = 0; i < testList.size(); i++) {
                            FocLabTest test = (FocLabTest) testList.getFocObject(i);
                            if (!firstTest) {
                                jsonBuilder.append(",");
                            }
                            firstTest = false;

                            jsonBuilder.append("{");
                            jsonBuilder.append("\"testCode\":\"").append(test.getLabel() != null ? test.getLabel() : "").append("\",");
                            jsonBuilder.append("\"testDesc\":\"").append(test.getPropertyString("test_label") != null ? test.getPropertyString("test_label") : "").append("\"");
                            jsonBuilder.append("}");
                        }
                    }
                    jsonBuilder.append("]");

                    jsonBuilder.append("}");
                }
            }

            jsonBuilder.append("]");
            jsonBuilder.append("}");

            return jsonBuilder.toString();

        } catch (Exception e) {
            throw new IOException("Error converting L3Message to JSON: " + e.getMessage(), e);
        }
    }
}
