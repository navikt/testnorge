package no.nav.registre.sdforvalter.config.credentials;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "organisasjon.api")
public class OrganisasjonApiServerProperties extends NaisServerProperties {
    private Integer threads;
}
