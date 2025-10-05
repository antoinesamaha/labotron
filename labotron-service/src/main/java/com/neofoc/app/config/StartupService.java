package com.neofoc.app.config;

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
    }

}
