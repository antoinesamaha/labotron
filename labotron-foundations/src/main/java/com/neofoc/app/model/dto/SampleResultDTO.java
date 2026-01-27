package com.neofoc.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Data Transfer Object representing the results for a sample,
 * containing a collection of test results.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SampleResultDTO {
    
    /**
     * The ID of the sample
     */
    private String sampleId;
    
    /**
     * List of test results for this sample
     */
    private List<TestResultDTO> tests;
}
