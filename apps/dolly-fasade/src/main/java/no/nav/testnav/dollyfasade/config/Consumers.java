package no.nav.testnav.dollyfasade.config;

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

    private ServerProperties identPool;
    private ServerProperties testnavPdlForvalter;
    private ServerProperties testnavPdlProxy;
    private ServerProperties testnavPersonService;
    private ServerProperties testnavTpsMessagingService;
    private ServerProperties yrkesskadeProxy;
    private ServerProperties arbeidssoekerregisteretProxy;
    private ServerProperties brukerService;
    private ServerProperties safProxy;
    private ServerProperties etterlatte;
    private ServerProperties nomProxy;
    private ServerProperties teamkatalog;
    private ServerProperties altinn3TilgangService;
}