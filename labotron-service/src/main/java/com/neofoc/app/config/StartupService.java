package com.neofoc.app.config;

import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Scheduled;
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

    @Scheduled(fixedDelay = 5000)
    public void refreshInstrumentConnectivity() {
        FocDesc focDesc = FocInstrument.getFocDesc();
        if (focDesc == null) return;
        FocList list = focDesc.getFocList();
        if (list == null) return;
        for (int i = 0; i < list.size(); i++) {
            FocInstrument instrument = (FocInstrument) list.getFocObject(i);
            if (Boolean.TRUE.equals(instrument.getStarted())) {
                try {
                    instrument.refreshConnectedFlag();
                } catch (Exception ignored) {}
            }
        }
    }

}
