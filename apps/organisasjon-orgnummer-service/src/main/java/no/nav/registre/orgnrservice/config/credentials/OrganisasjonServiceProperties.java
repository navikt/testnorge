package no.nav.registre.orgnrservice.config.credentials;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Setter
@Getter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-organisasjon-service")
public class OrganisasjonServiceProperties extends ServerProperties {
    private Integer threads;
}
