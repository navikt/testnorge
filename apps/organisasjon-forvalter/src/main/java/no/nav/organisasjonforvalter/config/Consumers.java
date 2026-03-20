package no.nav.organisasjonforvalter.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.val;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

/**
 * Samler alle placeholders for ulike {@code consumers.*}-konfigurasjon her, dvs. subklasser av {@code ServerProperties}.
 * <br/><br/>
 * Husk at Spring Boot bruker <a href="https://docs.spring.io/spring-boot/docs/current/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding">relaxed binding</a>
 * mellom configuration properties og field names.
 *
 * @see ServerProperties
 */
@Configuration
@ConfigurationProperties(prefix = "consumers")
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter(PACKAGE)
public class Consumers {

    private ServerProperties testnavAdresseService;
    private ServerProperties genererNavnService;
    private ServerProperties testnavMiljoerService;
    private ServerProperties organisasjonBestillingService;
    private ServerProperties testnavOrganisasjonService;
    private ServerProperties testnavOrgnummerService;
    private ExtendedServerProperties eregServices;

    public static class ExtendedServerProperties extends ServerProperties {

        public ServerProperties getMiljoe(String miljoe) {

            val serverProperties = new ServerProperties();
            if ("q2".equals(miljoe)) {
                serverProperties.setUrl(getUrl().replace("-{miljoe}", ""));
                serverProperties.setName(getName().replace("-{miljoe}", ""));
            } else {
                serverProperties.setUrl(getUrl().replace("{miljoe}", miljoe));
                serverProperties.setName(getName().replace("{miljoe}", miljoe));
            }
            serverProperties.setCluster(getCluster());
            serverProperties.setNamespace(getNamespace());
            return serverProperties;
        }
    }
}
