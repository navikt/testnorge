package no.nav.registre.bisys.config.credential;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "elasticsearch.client")
public class ElasticSearchCredentials {
    private String host;
    private String port;
    private String username;
    private String password;
}

