package no.nav.dolly.config.credentials;

import lombok.Getter;
import lombok.Setter;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-service")
public class OrganisasjonServiceProperties extends NaisServerProperties {
    private Integer threads;
}