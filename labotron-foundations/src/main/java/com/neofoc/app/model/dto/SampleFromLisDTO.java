package com.neofoc.app.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SampleFromLisDTO {

    private String sampleId;
    private String sampleType;
    private String patientId;
    private String firstName;
    private String lastName;
    private String middleInitial;
    private String dateOfBirth;
    private String sex;
    private String currentDateTime;
    private String collectionDate;
    private String origin;

    private List<TestFromLisDTO> tests;

}
