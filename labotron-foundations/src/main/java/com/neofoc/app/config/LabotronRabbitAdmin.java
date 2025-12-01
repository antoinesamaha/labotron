package com.neofoc.app.config;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import lombok.Getter;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LabotronRabbitAdmin extends RabbitAdmin {

    private final Map<String, Queue> driver2InstrumentQueues = new HashMap<>();
//    @Getter
//    private Queue driver2ConnectorQueue;

    public LabotronRabbitAdmin(ConnectionFactory connectionFactory, LabotronProperties labotronProperties) {
        super(connectionFactory);
    }

    private boolean doCreateQueue(FocInstrument instrument) {
        boolean inquiryBased = false;
        try {
            inquiryBased = instrument.getDriver().isInquiryBased();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return !inquiryBased;
    }

    public void createInstrumentQueues() {
        FocDesc instrumentDesc = Globals.getApp().getFocDescByName("instrument");
        // Load all instruments from the database
        FocList instruments = instrumentDesc.getFocList();
        instruments.loadIfNotLoadedFromDB();

        for (int i=0; i<instruments.size(); i++) {
            FocInstrument instrument = (FocInstrument) instruments.getFocObject(i);

            if (doCreateQueue(instrument)) {
                String instrumentCode = instrument.getCode();

                // Create send queue (Labotron to Instrument)
                Queue sendQueue = new Queue(
                        "connector-2-" + instrumentCode,
                        true,   // durable
                        false,  // not exclusive
                        false   // not auto-delete
                );
                driver2InstrumentQueues.put(instrumentCode, sendQueue);
                declareQueue(sendQueue);
            }
        }

//        // Create receive queue (Instrument to Labotron)
//        driver2ConnectorQueue = new Queue(
//                "drivers.to.connector",
//                true,   // durable
//                false,  // not exclusive
//                false   // not auto-delete
//        );
//        declareQueue(driver2ConnectorQueue);
    }

    public Queue getDriver2InstrumentQueue(String instrumentCode) {
        return driver2InstrumentQueues.get(instrumentCode);
    }

}
