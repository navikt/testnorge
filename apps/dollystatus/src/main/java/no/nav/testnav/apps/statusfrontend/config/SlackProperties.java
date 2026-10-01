package no.nav.testnav.apps.statusfrontend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    private boolean enabled;
    private String token;
    private String channel;
    private String baseUrl;
    private String statusPageUrl;
}
