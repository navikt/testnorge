package no.nav.registre.testnorge.helsepersonellservice.config.credentials;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-samhandlerregisteret-proxy")
public class SamhandlerregisteretServerProperties extends NaisServerProperties {
    private Integer threads;
}
