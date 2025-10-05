package com.neofoc.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foc.Globals;
import com.neofoc.app.config.LabotronRabbitAdmin;
import com.neofoc.app.model.dto.SampleFromLisDTO;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.AmqpHeaders;
import com.rabbitmq.client.Channel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import com.neofoc.app.config.LabotronProperties;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@ConditionalOnProperty(name = "labotron.connector.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQListenerService {

    private final LabotronRabbitAdmin labotronRabbitAdmin;
    private final LabotronProperties labotronProperties;
    private final ConnectorService connectorService;
    private final ConnectionFactory connectionFactory;
    private final CommunicationLogService communicationLogService;

    private Map<String, RabbitMQInstrumentListenerContainer> lis2InstrumentListeners = new HashMap<>();

    @Autowired
    public RabbitMQListenerService(LabotronRabbitAdmin labotronRabbitAdmin, LabotronProperties labotronProperties, ConnectorService connectorService, ConnectionFactory connectionFactory, CommunicationLogService communicationLogService) {
        this.labotronProperties = labotronProperties;
        this.connectorService = connectorService;
        this.labotronRabbitAdmin = labotronRabbitAdmin;
        this.connectionFactory = connectionFactory;
        this.communicationLogService = communicationLogService;
    }

    @RabbitListener(queues = "#{@labotronProperties.connector.lis2LabotronQueue}", ackMode = "MANUAL")
    public void receiveMessageFromLISQueue(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String sampleId = null;
        String body = null;
        try {
            body = new String(message.getBody());
            log.info("Received: {}", body);

            // Parse JSON into DTO
            ObjectMapper objectMapper = new ObjectMapper();
            SampleFromLisDTO sampleFromLis = objectMapper.readValue(body, SampleFromLisDTO.class);
            sampleId = sampleFromLis.getSampleId();
            Globals.logString("Parsed sample ID: " + sampleId);
            Globals.logString("Number of tests: " + String.valueOf(sampleFromLis.getTests().size()));

            // Process the DTO as needed
            connectorService.processSampleFromLis(sampleFromLis);

            // Acknowledge the message when done
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            Globals.logException(e);

            // Optionally, do not acknowledge to requeue
            try {
                log.error("Error processing message: "+e.getMessage(), e);
                // Negative acknowledgment - message will be requeued
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                Globals.logException(ex);
            }
        } finally {
            communicationLogService.log(communicationLogService.RECEIVED_LIS_2_CONNECTOR, null, null, sampleId, body);
        }
    }

    public void startInstrumentListener(FocInstrument instrument) {
        RabbitMQInstrumentListenerContainer rabbitMQInstrumentListenerContainer = lis2InstrumentListeners.get(instrument.getCode());

        if(rabbitMQInstrumentListenerContainer == null) {
            String instrumentCode = instrument.getCode();
            Queue queue = labotronRabbitAdmin.getSendingQueueForInstrument(instrumentCode); //Ensure Queue is created
            rabbitMQInstrumentListenerContainer = new RabbitMQInstrumentListenerContainer(instrument, queue.getName());
            lis2InstrumentListeners.put(instrumentCode, rabbitMQInstrumentListenerContainer);
        }

        rabbitMQInstrumentListenerContainer.startListening(connectionFactory);
    }

    public void stopInstrumentListener(String instrumentCode) {
        RabbitMQInstrumentListenerContainer rabbitMQInstrumentListenerContainer = lis2InstrumentListeners.get(instrumentCode);
        if(rabbitMQInstrumentListenerContainer != null) {
            rabbitMQInstrumentListenerContainer.stopListening();
        }
    }
}