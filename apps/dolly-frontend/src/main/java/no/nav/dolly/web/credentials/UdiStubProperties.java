package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.ServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.udi-stub")
public class UdiStubProperties extends ServerProperties {
}