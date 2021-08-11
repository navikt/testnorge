package no.nav.dolly.web.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.testnav-arena-forvalteren-proxy")
public class TestnavArenaForvalterenProxyProperties extends NaisServerProperties {
}