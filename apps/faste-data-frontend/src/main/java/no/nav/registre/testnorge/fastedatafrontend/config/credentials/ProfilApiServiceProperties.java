package no.nav.registre.testnorge.fastedatafrontend.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-profil-api")
public class ProfilApiServiceProperties extends NaisServerProperties {
}