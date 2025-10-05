package com.neofoc.app.service;

import com.foc.Globals;
import com.neofoc.app.driver.IDriver;
import com.neofoc.app.modules.labotron.focObjects.FocInstrument;
import com.neofoc.app.modules.labotron.focObjects.L3Message;
import com.neofoc.app.modules.labotron.utils.MessageConverter;
import com.neofoc.app.utils.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareMessageListener;
import com.rabbitmq.client.Channel;
import java.io.IOException;

@Slf4j
public class RabbitMQInstrumentListenerContainer {
    private SimpleMessageListenerContainer container;
    private FocInstrument instrument;
    private String queueName;
    private IDriver driver;

    public RabbitMQInstrumentListenerContainer(FocInstrument instrument, String queueName) {
        this.queueName = queueName;
        this.instrument = instrument;
    }

    protected IDriver getDriver(){
        if (driver == null) {
            try {
                driver = instrument != null ? instrument.getDriver() : null;
            } catch (Exception e) {
                Globals.logException(e);
            }
        }
        return driver;
    }

    //RabbitMQ listener
    public void startListening(ConnectionFactory connectionFactory) {
        if (container == null) {
            container = new SimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(queueName);
            container.setAcknowledgeMode(AcknowledgeMode.MANUAL);

            container.setMessageListener((ChannelAwareMessageListener) (message, channel) -> {
                String body = new String(message.getBody());
                CommunicationLogService communicationLogService = SpringContextUtil.getBean(CommunicationLogService.class);

                // Get messageId from message properties
                String messageId = message.getMessageProperties().getMessageId();
                log.info("Instrument {} Received messageId {}: {}", instrument.getCode(), messageId, body);

                try {
                    IDriver driverInstance = getDriver();
                    if (driverInstance == null || !driverInstance.isConnected()) {
                        if (driverInstance == null) {
                            log.error("Driver not found for instrument {}", instrument.getCode());
                        }
                        if (!driverInstance.isConnected()) {
                            log.error("Driver not connected for instrument {}", instrument.getCode());
                        }
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                        return;
                    }

                    L3Message l3Message = MessageConverter.convertJsonToL3Message(body);

                    // Convert l3Message to JSON for logging
                    String l3MessageJson = MessageConverter.convertL3MessageToJson(l3Message);
                    driverInstance.send(l3Message);

                    if (messageId != null && !messageId.isEmpty()) {
                        communicationLogService.logReception(messageId);
                    }
                    communicationLogService.log(communicationLogService.SENT_DRIVER_2_INSTRUMENT, null, instrument,
                                               l3Message.getSample(0).getSampleId(), l3MessageJson);

                    // Manual acknowledgment
                    channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
                    log.info("Sent to Instrument {}: {}", queueName, body);
                } catch (Exception e) {
                    log.error("Error processing message from queue: {}", e.getMessage(), e);
                    try {
                        // Negative acknowledgment, requeue the message
                        channel.basicNack(message.getMessageProperties().getDeliveryTag(), false, true);
                    } catch (IOException nackException) {
                        log.error("Error sending NACK: {}", nackException.getMessage(), nackException);
                    }
                }
            });
        }
        container.start();
    }

    public void stopListening() {
        if (container != null) {
            container.stop();
        }
    }
}
