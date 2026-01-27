package com.neofoc.app.controller;

import com.foc.list.FocList;
import com.foc.shared.json.B01JsonBuilder;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import com.neofoc.app.service.LabSampleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/labsample")
@CrossOrigin(origins = "*")
@Slf4j
public class LabSampleController {

    private final LabSampleService labSampleService;

    public LabSampleController(LabSampleService labSampleService) {
        this.labSampleService = labSampleService;
    }

    /**
     * Get LabSample objects, optionally filtered by sample_id contains
     * @param request HTTP request
     * @param sampleId Optional query parameter to filter by sample_id (case-insensitive contains)
     * @return ResponseEntity with list of FocLabSample objects
     */
    @GetMapping
    protected ResponseEntity<String> getLabSamples(
            HttpServletRequest request,
            @RequestParam(required = false) String sampleId) {

        try {
            FocList samples;
            
            if (sampleId != null && !sampleId.isEmpty()) {
                log.info("Fetching LabSamples with sample_id containing: {}", sampleId);
                samples = labSampleService.findBySampleIdContains(sampleId);
            } else {
                log.info("Fetching all LabSamples");
                samples = labSampleService.findAll();
            }

            if (samples.isEmpty()) {
                log.debug("No LabSamples found");

                samples.dispose();

                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("");
            }

            log.info("Found {} LabSamples", samples.size());

            B01JsonBuilder builder = new B01JsonBuilder();
            samples.toJson(builder);
            String result = builder.toString();

            String responseBody = "{ \"data\":" + result + ", \"totalCount\":" + samples.size()
                    + "}";

            samples.dispose();

            return ResponseEntity.ok(responseBody);

        } catch (Exception e) {
            log.error("Error fetching LabSamples: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();
        }
    }
}
