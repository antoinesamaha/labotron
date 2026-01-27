package com.neofoc.app.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for Labotron
 * Maps all properties under "labotron" prefix in application.yml
 */
@Component
@ConfigurationProperties(prefix = "labotron")
@Getter
@Setter
public class LabotronProperties {

    private ConnectorProperties connector = new ConnectorProperties();

    /**
     * Nested class for connector properties
     */
    @Getter
    @Setter
    public static class ConnectorProperties {
        private boolean enabled;
        private String lis2ConnectorQueue;
        private String driver2ConnectorQueue;
        private String connector2LisQueue;
    }
}
