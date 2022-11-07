package no.nav.registre.sdforvalter.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.securitycore.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-aaregister-proxy")
public class AaregProperties extends ServerProperties{
}