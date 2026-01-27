package com.neofoc.app.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class TestFromLisDTO {

    @JsonProperty("TEST_CODE")
    private String testCode;

    @JsonProperty("TEST_DESC")
    private String testDesc;

    @JsonIgnore
    @JsonProperty("ANALYZER_CODE")
    private String suggestedInstrumentCode;

    @JsonIgnore
    @JsonProperty("ACTUAL_ANALYZER_CODE")
    private String instrumentCode;

}
