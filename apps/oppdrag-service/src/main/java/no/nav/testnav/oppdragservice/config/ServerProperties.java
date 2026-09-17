package no.nav.testnav.oppdragservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "oppdrag-service")
public class ServerProperties {

    private String host;
    private Map<String, String> ports;
}