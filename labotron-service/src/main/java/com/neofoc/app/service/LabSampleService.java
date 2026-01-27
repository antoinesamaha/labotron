package com.neofoc.app.service;

import com.foc.db.ListPagination;
import com.foc.desc.FocDesc;
import com.foc.list.FocLinkSimple;
import com.foc.list.FocList;
import com.foc.list.FocListOrder;
import com.neofoc.app.modules.labotron.focObjects.FocLabSample;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class LabSampleService {

    /**
     * Find LabSample objects where sample_id contains the given search string
     * @param sampleId The search string to filter by (case-insensitive LIKE query)
     * @return List of FocLabSample objects matching the filter
     */
    public FocList findBySampleIdContains(String sampleId) {
        log.debug("Finding LabSamples with sample_id containing: {}", sampleId);
        
        FocDesc focDesc = FocLabSample.getFocDesc();
        FocList list = new FocList(new FocLinkSimple(focDesc));
        
        // Apply filter if sampleId is provided
        if (sampleId != null && !sampleId.isEmpty()) {
            String whereClause = "LOWER(sample_id) LIKE '%" + sampleId.toLowerCase() + "%'";
            list.getFilter().putAdditionalWhere("SAMPLE_ID_FILTER", whereClause);
        }

        ListPagination listPagination = list.getFilter().getPagination(true);
        listPagination.setOffset(0);
        listPagination.setPageNbrOfRows(100);
        
        // Load from database
        list.loadIfNotLoadedFromDB();

        log.debug("Found {} LabSamples", list.size());
        return list;
    }

    /**
     * Find all LabSample objects
     * @return List of all FocLabSample objects
     */
    public FocList findAll() {
        return findBySampleIdContains(null);
    }
}
