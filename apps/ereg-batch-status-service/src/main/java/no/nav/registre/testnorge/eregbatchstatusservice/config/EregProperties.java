package no.nav.registre.testnorge.eregbatchstatusservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "ereg")
public class EregProperties {
    private Map<String, String> envHostMap;
}
