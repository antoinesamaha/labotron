package com.neofoc.app.controller;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.model.dto.InstrumentStartDTO;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.service.InstrumentReceiverListener;
import com.neofoc.app.service.RabbitMQListenerService;
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

    private final RabbitMQListenerService rabbitMQListenerService;

    public InstrumentController(RabbitMQListenerService rabbitMQListenerService) {
        this.rabbitMQListenerService = rabbitMQListenerService;
    }

    @PostMapping("start")
    protected ResponseEntity<String> doPost(HttpServletRequest request, @RequestBody InstrumentStartDTO instrumentStartDTO)
            throws ServletException, IOException {

        long instrumentId = instrumentStartDTO.getInstrumentId();
        FocDesc focDesc = Globals.getApp().getFocDescByName("instrument");
        FocList list = focDesc.getFocList();
        FocInstrument instrument = (FocInstrument) list.searchByReference(instrumentId);

        try {
            instrument.getDriver();// To create the driver if not available yet
            rabbitMQListenerService.startInstrumentListener(instrument);
            instrument.addMessageListener(new InstrumentReceiverListener(instrument));
            instrument.getDriver().connect();
        } catch (Exception e) {
            log.error("Error starting instrument driver connect and RabbitMQ listener: {}", e.getMessage(), e);
        }

        return ResponseEntity.ok().build();
    }
}
