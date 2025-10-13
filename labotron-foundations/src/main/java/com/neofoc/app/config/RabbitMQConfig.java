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
    public Queue getConnector2LisQueue() {
        return new Queue(labotronProperties.getConnector().getConnector2LisQueue(), true, false, false);
    }

    @Bean
    public Queue getDriver2ConnectorQueue() {
        return new Queue(labotronProperties.getConnector().getDriver2ConnectorQueue(), true, false, false);
    }

    @Bean
    public Queue getLis2ConnectorQueue() {//Only for creation
        return new Queue(labotronProperties.getConnector().getLis2ConnectorQueue(), true, false, false);  // (name, durable, exclusive, auto-delete)
    }

}