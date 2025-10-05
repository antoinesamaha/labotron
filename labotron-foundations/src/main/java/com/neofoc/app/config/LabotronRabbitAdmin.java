package com.neofoc.app.config;

import com.foc.Globals;
import com.foc.desc.FocDesc;
import com.foc.list.FocList;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LabotronRabbitAdmin extends RabbitAdmin {

    private Map<String, Queue> sendingQueues = new HashMap<>();
    private Map<String, Queue> receivingQueues = new HashMap<>();

    public LabotronRabbitAdmin(ConnectionFactory connectionFactory, LabotronProperties labotronProperties) {
        super(connectionFactory);
    }

    public void createInstrumentQueues() {
        FocDesc instrumentDesc = Globals.getApp().getFocDescByName("instrument");
        // Load all instruments from the database
        FocList instruments = instrumentDesc.getFocList();
        instruments.loadIfNotLoadedFromDB();

        for (int i=0; i<instruments.size(); i++) {
            FocInstrument instrument = (FocInstrument) instruments.getFocObject(i);

            String instrumentCode = instrument.getCode();

            // Create send queue (Labotron to Instrument)
            Queue sendQueue = new Queue(
                    "labotron.to." + instrumentCode,
                    true,   // durable
                    false,  // not exclusive
                    false   // not auto-delete
            );
            sendingQueues.put(instrumentCode, sendQueue);
            declareQueue(sendQueue);

            // Create receive queue (Instrument to Labotron)
            Queue receiveQueue = new Queue(
                    instrumentCode + ".to.labotron",
                    true,   // durable
                    false,  // not exclusive
                    false   // not auto-delete
            );
            receivingQueues.put(instrumentCode, receiveQueue);
            declareQueue(receiveQueue);
        }
    }

    public Queue getSendingQueueForInstrument(String instrumentCode) {
        return sendingQueues.get(instrumentCode);
    }

    public Queue getReceivingQueueForInstrument(String instrumentCode) {
        return receivingQueues.get(instrumentCode);
    }

}
