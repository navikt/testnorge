package no.nav.testnav.apps.personservice.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;

@Configuration
@ConfigurationProperties(prefix = "consumers.tps-forvalteren-proxy")
public class TpsForvalterenProxyServiceProperties extends NaisServerProperties {
}