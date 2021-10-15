package no.nav.registre.testnorge.oppsummeringsdokumentservice.config.credentials;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "spring.data.elasticsearch.client.reactive")
public class ElasticSearchCredentials {
    private String endpoints;
    private String username;
    private String password;
}
