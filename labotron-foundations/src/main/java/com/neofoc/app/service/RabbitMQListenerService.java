package com.neofoc.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.foc.Globals;
import com.neofoc.app.config.LabotronRabbitAdmin;
import com.neofoc.app.model.dto.SampleFromLisDTO;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.utils.SpringContextUtil;
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

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
//@ConditionalOnProperty(name = "labotron.connector.enabled", havingValue = "true", matchIfMissing = false)
public class RabbitMQListenerService {

    private final LabotronRabbitAdmin labotronRabbitAdmin;
    private final LabotronProperties labotronProperties;
    private final ConnectorService connectorService;
    private final ConnectionFactory connectionFactory;
    private final CommunicationLogService communicationLogService;
    private final ObjectMapper objectMapper;

    private Map<String, RabbitMQInstrumentListenerContainer> lis2InstrumentListeners = new HashMap<>();

    @Autowired
    public RabbitMQListenerService(LabotronRabbitAdmin labotronRabbitAdmin, LabotronProperties labotronProperties, ConnectorService connectorService, ConnectionFactory connectionFactory, CommunicationLogService communicationLogService) {
        this.labotronProperties = labotronProperties;
        this.connectorService = connectorService;
        this.labotronRabbitAdmin = labotronRabbitAdmin;
        this.connectionFactory = connectionFactory;
        this.communicationLogService = communicationLogService;

        // Initialize ObjectMapper with JavaTimeModule for handling Java 8 date/time types
        this.objectMapper = new ObjectMapper();

        // Configure custom date format patterns for LocalDateTime
        JavaTimeModule javaTimeModule = new JavaTimeModule();

        // Define formatters for different date patterns in incoming JSON
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        // Register deserializers with custom formatters
        LocalDateTimeDeserializer dateDeserializer = new LocalDateTimeDeserializer(dateFormatter);

        // Replace the default deserializer with our custom one
        javaTimeModule.addDeserializer(LocalDateTime.class, dateDeserializer);

        objectMapper.registerModule(javaTimeModule);
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @RabbitListener(queues = "#{@labotronProperties.connector.lis2ConnectorQueue}", ackMode = "MANUAL")
    public void lis2ConnectorQueueListener(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String sampleId = null;
        String body = null;
        try {
            body = new String(message.getBody());
            log.info("Received: {}", body);

            // Parse JSON into DTO using the configured ObjectMapper
            SampleFromLisDTO sampleFromLis = objectMapper.readValue(body, SampleFromLisDTO.class);
            sampleId = sampleFromLis.getSampleId();
            Globals.logString("Parsed sample ID: " + sampleId);
            Globals.logString("Number of tests: " + String.valueOf(sampleFromLis.getTests().size()));

            communicationLogService.log(communicationLogService.RECEIVED_LIS_2_CONNECTOR, null, null, sampleId, body);

            // Process the DTO as needed
            connectorService.processSampleFromLis(sampleFromLis);

            // Acknowledge the message when done
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            Globals.logException(e);

            try {
                log.error("Error processing message: "+e.getMessage());
                Globals.logException(e);

                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                Globals.logException(ex);
            }
        }
    }

    @RabbitListener(queues = "#{@labotronProperties.connector.driver2ConnectorQueue}", ackMode = "MANUAL")
    public void driver2ConnectorQueueListener(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag) {
        String body = null;
        try {
            body = new String(message.getBody());
            log.info("Received: {}", body);

            RabbitMQSendingService rmqSendingService = SpringContextUtil.getBean(RabbitMQSendingService.class);
            rmqSendingService.sendToLis(null, body);

            // Acknowledge the message when done
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            Globals.logException(e);

            try {
                Globals.logException(e);
                log.error("Error processing message: "+e.getMessage(), e);
                channel.basicNack(deliveryTag, false, true);
            } catch (Exception ex) {
                Globals.logException(ex);
            }
        }
    }

    public void startInstrumentListener(FocInstrument instrument) {
        RabbitMQInstrumentListenerContainer rabbitMQInstrumentListenerContainer = lis2InstrumentListeners.get(instrument.getCode());

        if(rabbitMQInstrumentListenerContainer == null) {
            String instrumentCode = instrument.getCode();
            Queue queue = labotronRabbitAdmin.getDriver2InstrumentQueue(instrumentCode); //Ensure Queue is created
            rabbitMQInstrumentListenerContainer = new RabbitMQInstrumentListenerContainer(instrument, queue.getName());
            lis2InstrumentListeners.put(instrumentCode, rabbitMQInstrumentListenerContainer);
        }

        rabbitMQInstrumentListenerContainer.startListening(connectionFactory);
    }

    public void stopInstrumentListener(FocInstrument instrument) {
        RabbitMQInstrumentListenerContainer rabbitMQInstrumentListenerContainer = lis2InstrumentListeners.get(instrument.getCode());
        if(rabbitMQInstrumentListenerContainer != null) {
            rabbitMQInstrumentListenerContainer.stopListening();
        }
    }

    public boolean isInstrumentListenerConnected(FocInstrument instrument) {
        RabbitMQInstrumentListenerContainer rabbitMQInstrumentListenerContainer = lis2InstrumentListeners.get(instrument.getCode());
        return rabbitMQInstrumentListenerContainer != null && rabbitMQInstrumentListenerContainer.isListening();
    }
}