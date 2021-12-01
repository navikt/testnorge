package no.nav.registre.testnorge.tilbakemeldingapi.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.profil-api")
public class ProfilServiceProperties extends ServerProperties {
}
