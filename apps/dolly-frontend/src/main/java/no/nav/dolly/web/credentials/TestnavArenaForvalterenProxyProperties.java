package no.nav.dolly.web.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-arena-forvalteren-proxy")
public class TestnavArenaForvalterenProxyProperties extends NaisServerProperties {
}