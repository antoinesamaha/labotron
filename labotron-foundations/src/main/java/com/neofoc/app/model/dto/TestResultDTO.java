package com.neofoc.app.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object representing the result of a single test.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TestResultDTO {
    
    /**
     * The actual ID of the test
     */
    private String testId;
    
    /**
     * The status of the test (e.g., "COMPLETED", "PENDING", "ERROR")
     */
    private String status;
    
    /**
     * The code of the analyzer that performed the test
     */
    private String actualAnalyzerCode;
    
    /**
     * Flag indicating if there's an alarm/warning for this test result
     */
    private Boolean alarm;
    
    /**
     * The result value, can be numeric or string
     */
    private Object result;
    
    /**
     * Additional notes about the test
     */
    private String notes;
    
    /**
     * The unit of measurement for the result
     */
    private String unit;
    
    /**
     * Any message related to the test (e.g., error messages)
     */
    private String message;
    
    /**
     * Flag indicating if the result requires verification
     */
    private Boolean verificationPending;
}
