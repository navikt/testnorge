package no.nav.registre.testnorge.avhengighetsanalysefrontend.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.security.domain.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-profil-api")
public class ProfilApiServiceProperties extends NaisServerProperties {
}