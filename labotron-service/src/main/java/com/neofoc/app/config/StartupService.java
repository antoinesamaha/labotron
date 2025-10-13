package com.neofoc.app.config;

import com.foc.desc.FocDesc;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class StartupService {//} implements ApplicationRunner {

    private final LabotronRabbitAdmin rabbitAdmin;

    public StartupService(LabotronRabbitAdmin rabbitAdmin) {
        this.rabbitAdmin = rabbitAdmin;
    }

    @EventListener(ApplicationReadyEvent.class)
    @Order(2)
    public void runAfterStartup() {
        rabbitAdmin.createInstrumentQueues();
        FocInstrument.applyStartedFlagForAllInstruments();
//        FocInstrument.refreshStartedFlagForAllInstruments();
    }

}
