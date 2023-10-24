package no.nav.testnav.endringsmeldingservice.config;

import lombok.NoArgsConstructor;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

@NoArgsConstructor(access = PACKAGE)
public class Consumers {

    @Configuration
    @ConfigurationProperties(prefix = "consumers.tps-forvalteren-proxy")
    public static class TpsForvalterenProxyService extends ServerProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "consumers.tps-messaging-service")
    public static class TpsMessagingService extends ServerProperties {
    }

}
