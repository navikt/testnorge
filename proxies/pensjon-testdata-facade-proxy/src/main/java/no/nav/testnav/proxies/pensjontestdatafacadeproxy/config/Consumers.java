package no.nav.testnav.proxies.pensjontestdatafacadeproxy.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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

    private SamboerServerProperties samboerTestdata;
    private AfpOffentligServerProperties afpOffentlig;
    private ServerProperties pensjonTestdataFacade;

    public static class SamboerServerProperties extends ServerProperties {

        public ServerProperties getMiljoe(String env) {

            return ServerProperties.of(
                    this.getCluster(),
                    this.getNamespace(),
                    this.getName().replace("{miljoe}", env),
                    this.getUrl().replace("{miljoe}", env)
            );
        }
    }

    public static class AfpOffentligServerProperties extends ServerProperties {

        public ServerProperties getMiljoe(String env) {

            return ServerProperties.of(
                    this.getCluster(),
                    this.getNamespace(),
                    this.getName().replace("{miljoe}", env),
                    this.getUrl().replace("{miljoe}", env)
            );
        }
    }
}
