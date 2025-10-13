package com.neofoc.app.controller;

import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("api/instrument")
@CrossOrigin(origins = "*")
@Slf4j
public class InstrumentController {

    public InstrumentController() {
    }

    @PostMapping("refreshStartedFlag")
    protected ResponseEntity<String> refreshStartedFlag(HttpServletRequest request)
            throws ServletException, IOException {

        FocInstrument.refreshStartedFlagForAllInstruments();

        return ResponseEntity.ok().build();
    }

    @PostMapping("{instrumentId}/start")
    protected ResponseEntity<String> start(HttpServletRequest request, @PathVariable long instrumentId)
            throws ServletException, IOException {

        FocDesc focDesc = FocInstrument.getFocDesc();
        FocList list = focDesc.getFocList();
        FocInstrument instrument = (FocInstrument) list.searchByReference(instrumentId);

        try {
            instrument.switchOn();
        } catch (Exception e) {
            log.error("Error starting instrument driver connect and RabbitMQ listener: {}", e.getMessage(), e);
        }

        return instrument != null && instrument.getStarted() ? ResponseEntity.ok().build() : ResponseEntity.status(500).body("Failed to connect to instrument");
    }

    @PostMapping("{instrumentId}/stop")
    protected ResponseEntity<String> stop(HttpServletRequest request, @PathVariable long instrumentId)
            throws ServletException, IOException {

        FocDesc focDesc = FocInstrument.getFocDesc();
        FocList list = focDesc.getFocList();
        FocInstrument instrument = (FocInstrument) list.searchByReference(instrumentId);

        try {
            instrument.switchOff();
        } catch (Exception e) {
            log.error("Error starting instrument driver connect and RabbitMQ listener: {}", e.getMessage(), e);
        }

        return instrument != null && !instrument.getStarted() ? ResponseEntity.ok().build() : ResponseEntity.status(500).body("Failed to connect to instrument");
    }
}
