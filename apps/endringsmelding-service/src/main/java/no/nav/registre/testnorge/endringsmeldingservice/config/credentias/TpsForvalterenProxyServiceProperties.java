package no.nav.registre.testnorge.endringsmeldingservice.config.credentias;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
@ConfigurationProperties(prefix = "consumers.tps-forvalteren-proxy")
public class TpsForvalterenProxyServiceProperties extends NaisServerProperties {
}