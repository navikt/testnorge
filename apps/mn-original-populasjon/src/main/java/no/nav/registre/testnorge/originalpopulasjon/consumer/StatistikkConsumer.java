package no.nav.registre.testnorge.originalpopulasjon.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.originalpopulasjon.config.credentials.StatistikkServiceProperties;
import no.nav.registre.testnorge.originalpopulasjon.config.credentials.SyntPersonServiceProperties;
import no.nav.registre.testnorge.originalpopulasjon.consumer.command.GetStatistikkCommand;
import no.nav.registre.testnorge.originalpopulasjon.exceptions.StatistikkException;

@Slf4j
@Component
@DependencyOn("statistikk-api")
public class StatistikkConsumer {

    private final WebClient webClient;
    private final StatistikkServiceProperties serviceProperties;
    private final AccessTokenService accessTokenService;

    StatistikkConsumer(
            StatistikkServiceProperties serviceProperties,
            AccessTokenService accessTokenService
    ) {
        this.accessTokenService = accessTokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient
                .builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    public StatistikkDTO getStatistikk(StatistikkType statistikkType) {
        log.info("Henter statistikk {}...", statistikkType);
        AccessToken accessToken = accessTokenService.generateToken(serviceProperties);
        return new GetStatistikkCommand(webClient, statistikkType, accessToken.getTokenValue()).call();
    }
}
