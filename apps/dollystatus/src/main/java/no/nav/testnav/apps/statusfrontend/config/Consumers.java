package no.nav.testnav.apps.statusfrontend.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;

@Configuration
@ConfigurationProperties(prefix = "consumers")
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter(PACKAGE)
public class Consumers {

    private ServerProperties testnavPdlForvalter;
    private ServerProperties testnavPdlProxy;
    private ServerProperties testnavDollyBackend;
    private ServerProperties testnavDollyProxy;
    private ServerProperties testnavArbeidssoekerregisteretProxy;
    private ServerProperties testnavArbeidsplassenCVProxy;
    private ServerProperties testnavNomProxy;
    private ServerProperties testnavOrganisasjonForvalter;
    private ServerProperties testnavTpsMessagingService;
}
