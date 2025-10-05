package com.neofoc.app.config;

import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final LabotronProperties labotronProperties;

    @Autowired
    RabbitMQConfig(LabotronProperties labotronProperties) {
        this.labotronProperties = labotronProperties;
    }

    @Bean
    public Queue send2LisQueue() {
        return new Queue(labotronProperties.getConnector().getLabotron2LisQueue(), true, false, false);
    }

    @Bean
    public Queue receiveFromLisQueue() {//Only for creation
        return new Queue(labotronProperties.getConnector().getLis2LabotronQueue(), true, false, false);  // (name, durable, exclusive, auto-delete)
    }

}