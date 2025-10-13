package com.neofoc.app.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
public class SampleFromLisDTO {

    private String sampleId;
    private String sampleType;
    private String patientId;
    private String firstName;
    private String lastName;
    private String middleInitial;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String sex;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime currentDateTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime collectionDate;
    private String origin;

    private List<TestFromLisDTO> tests;

}
