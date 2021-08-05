package no.nav.registre.testnorge.miljoerservice.config.credentias;

import no.nav.testnav.libs.servletsecurity.config.NaisServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConfigurationProperties(prefix = "consumers.tps-forvalteren-proxy")
public class TpsForvalterenProxyServiceProperties extends NaisServerProperties {
}