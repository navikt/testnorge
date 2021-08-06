package no.nav.testnav.endringsmeldingservice.config.credentias;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.testnav.libs.reactivesecurity.domain.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.tps-forvalteren-proxy")
public class TpsForvalterenProxyServiceProperties extends NaisServerProperties {
}