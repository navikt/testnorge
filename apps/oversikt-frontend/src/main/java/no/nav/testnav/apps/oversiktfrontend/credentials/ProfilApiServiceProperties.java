package no.nav.testnav.apps.oversiktfrontend.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnorge-profil-api")
public class ProfilApiServiceProperties extends ServerProperties {
}