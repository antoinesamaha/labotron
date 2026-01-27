package com.neofoc.app.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.neofoc.app.config.RabbitMQConfig;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.Queue;
import com.neofoc.app.config.LabotronRabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RabbitMQSendingService {

    private final RabbitTemplate rabbitTemplate;
    private final LabotronRabbitAdmin labotronRabbitAdmin;
    private final ObjectMapper objectMapper;
    private final CommunicationLogService communicationLogService;
    private final RabbitMQConfig rabbitMQConfig;

    public void sendToLis(String sampleId, String message) {
        rabbitTemplate.convertAndSend(rabbitMQConfig.getConnector2LisQueue().getName(), message);
        communicationLogService.log(CommunicationLogService.SENT_CONNECTOR_2_LIS, null, null, sampleId, message);
    }

    public void sendToConnector(String sampleId, String message) {
        rabbitTemplate.convertAndSend(rabbitMQConfig.getDriver2ConnectorQueue().getName(), message);
        communicationLogService.log(CommunicationLogService.SENT_DRIVER_2_CONNECTOR, null, null, sampleId, message);
    }

    public void sendToDriver(FocInstrument instrument, String instrumentCode, String sampleId, Object message) throws Exception {
        Queue queue = labotronRabbitAdmin.getDriver2InstrumentQueue(instrumentCode);
        String json = objectMapper.writeValueAsString(message);
        String msgId = UUID.randomUUID().toString();

        Message msg = MessageBuilder
                .withBody(json.getBytes(StandardCharsets.UTF_8))
                .setMessageId(msgId)
                .build();
        rabbitTemplate.convertAndSend(queue.getName(), msg);
        communicationLogService.log(CommunicationLogService.SENT_CONNECTOR_2_DRIVER, msgId, instrument, sampleId, json);
        //System.out.println("Published with message_id = " + msgId);
    }

}