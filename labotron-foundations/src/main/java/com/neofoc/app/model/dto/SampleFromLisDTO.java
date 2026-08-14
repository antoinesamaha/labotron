package com.neofoc.app.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SampleFromLisDTO {

    @JsonProperty("P_SAMPLE_ID")
    private String sampleId;

    @JsonProperty("SAMPLE_TYPE")
    private String sampleType;

    @JsonProperty("PATIENT_ID")
    private String patientId;

    @JsonProperty("PATIENT_NAME")
    private String patientName;

    @JsonProperty("FIRST_NAME")
    private String firstName;

    @JsonProperty("LAST_NAME")
    private String lastName;

    @JsonProperty("MIDDLE_INITIAL")
    private String middleInitial;

    //@JsonFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("DATE_OF_BIRTH")
    private LocalDate dateOfBirth;

    @JsonProperty("GENDER")
    private String sex;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    @JsonProperty("CURRENT_DATE_TIME")
    private LocalDateTime currentDateTime;

    //@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    //@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @JsonDeserialize(using = FlexibleLocalDateTimeDeserializer.class)
    @JsonProperty("COLLECTION_DATE")
    private LocalDateTime collectionDate;

    @JsonProperty("ORIGIN")
    private String origin;

    @JsonProperty("LISTEST")
    private List<TestFromLisDTO> tests;

    public void getFormattedPatientName() {
        StringBuilder formattedName = new StringBuilder();

        if (lastName != null && !lastName.isEmpty()) {
            formattedName.append(lastName);
        }

        if (firstName != null && !firstName.isEmpty()) {
            if (formattedName.length() > 0) {
                formattedName.append(", ");
            }
            formattedName.append(firstName);
        }

        if (middleInitial != null && !middleInitial.isEmpty()) {
            if (formattedName.length() > 0) {
                formattedName.append(" ");
            }
            formattedName.append(middleInitial).append(".");
        }

        patientName = formattedName.toString();
    }

    public void splitPatientName() {
        if (patientName != null && !patientName.isEmpty()) {
            String[] parts = patientName.split(" ");

            if (parts.length > 0) {
                firstName = parts[0].trim();
                lastName = parts[parts.length-1].trim();
            }

            if (parts.length > 2) {
                middleInitial = "";
                for (int i = 1; i < parts.length - 1; i++) {
                    String middlePart = parts[i].trim();
                    if (!middlePart.isEmpty()) {
                        middleInitial += " " + middlePart;
                    } else {
                        middleInitial = middlePart;
                    }
                }
            }
        }
    }

}

/*
Sample [SAMPLE+TESTS]:
 {
        "P_SAMPLE_ID" : "5647687",
        "CURRENT_DATE_TIME" : "04/12/2025",
        "SAMPLE_TYPE" : "SER",
        "COLLECTION_DATE" : "04/12/2025",
        "PATIENT_NAME" : "Said M Osman",
        "AGE" : "73",
        "GENDER" : "M",
        "PATIENT_ID" : "834121",
        "DATE_OF_BIRTH" : "17/08/1952",
        "ORIGIN" : "6FN",
        "LISTEST" : [
        {
        "SEQ_ID" : "40094876",
        "ACTUAL_TEST_ID" : "71347673",
        "SAMPLE_ID" : "5647687",
        "TEST_CODE" : "427",
        "TEST_DESC" : "Magnesium",
        "STATUS" : "0",
        "ANALYZER_CODE" : "",
        "ACTUAL_ANALYZER_CODE" : "",
        "RESULT" : "",
        "UNIT" : "",
        "MESSAGE" : "",
        "ALARM" : "",
        "PRIORITY" : "R",
        "VERIFICATION_PENDING" : "",
        "DATE_READ" : "04/12/2025",
        "RESULT_DATETIME" : "",
        "NOTES" : ""
        },
        {
        "SEQ_ID" : "40094877",
        "ACTUAL_TEST_ID" : "71347672",
        "SAMPLE_ID" : "5647687",
        "TEST_CODE" : "429",
        "TEST_DESC" : "Phosphorus",
        "STATUS" : "0",
        "ANALYZER_CODE" : "",
        "ACTUAL_ANALYZER_CODE" : "",
        "RESULT" : "",
        "UNIT" : "",
        "MESSAGE" : "",
        "ALARM" : "",
        "PRIORITY" : "R",
        "VERIFICATION_PENDING" : "",
        "DATE_READ" : "04/12/2025",
        "RESULT_DATETIME" : "",
        "NOTES" : ""
        },
        {
        "SEQ_ID" : "40094878",
        "ACTUAL_TEST_ID" : "71347670",
        "SAMPLE_ID" : "5647687",
        "TEST_CODE" : "405",
        "TEST_DESC" : "Calcium",
        "STATUS" : "0",
        "ANALYZER_CODE" : "",
        "ACTUAL_ANALYZER_CODE" : "",
        "RESULT" : "",
        "UNIT" : "",
        "MESSAGE" : "",
        "ALARM" : "",
        "PRIORITY" : "R",
        "VERIFICATION_PENDING" : "",
        "DATE_READ" : "04/12/2025",
        "RESULT_DATETIME" : "",
        "NOTES" : ""
        },
        {
        "SEQ_ID" : "40094879",
        "ACTUAL_TEST_ID" : "71347671",
        "SAMPLE_ID" : "5647687",
        "TEST_CODE" : "1116",
        "TEST_DESC" : "Corrected Calcium",
        "STATUS" : "0",
        "ANALYZER_CODE" : "",
        "ACTUAL_ANALYZER_CODE" : "",
        "RESULT" : "",
        "UNIT" : "",
        "MESSAGE" : "",
        "ALARM" : "",
        "PRIORITY" : "R",
        "VERIFICATION_PENDING" : "",
        "DATE_READ" : "04/12/2025",
        "RESULT_DATETIME" : "",
        "NOTES" : ""
        }
        ]
 }

 */