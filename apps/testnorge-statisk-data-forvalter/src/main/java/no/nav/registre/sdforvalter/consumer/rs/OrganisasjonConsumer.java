package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import no.nav.registre.sdforvalter.config.credentials.OrganisasjonApiServerProperties;
import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;
import no.nav.registre.testnorge.libs.common.command.GetOrganisasjonCommand;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessToken;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Slf4j
@Component
public class OrganisasjonConsumer {
    private final WebClient webClient;
    private final OrganisasjonApiServerProperties serverProperties;
    private final AccessTokenService accessTokenService;
    private final Executor executor;

    public OrganisasjonConsumer(
            OrganisasjonApiServerProperties serverProperties,
            AccessTokenService accessTokenService
    ) {
        this.serverProperties = serverProperties;
        this.accessTokenService = accessTokenService;
        this.executor = Executors.newFixedThreadPool(serverProperties.getThreads());
        this.webClient = WebClient
                .builder()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    private CompletableFuture<Organisasjon> getOrganisasjon(String orgnummer, String miljo, Executor executor) {
        AccessToken accessToken = accessTokenService.generateToken(serverProperties);
        return CompletableFuture.supplyAsync(
                () -> {
                    try {
                        return new Organisasjon(new GetOrganisasjonCommand(webClient, accessToken.getTokenValue(), orgnummer, miljo).call());
                    } catch (Exception e) {
                        log.warn("Klarer ikke å hente organisasjon {}.", orgnummer, e);
                        return null;
                    }
                },
                executor
        );
    }

    public Map<String, Organisasjon> getOrganisasjoner(List<String> orgnummerList, String miljo) {
        log.info("Henter ut {} fra ereg", String.join(", ", orgnummerList));

        AsyncOrganisasjonMap asyncMap = new AsyncOrganisasjonMap();
        orgnummerList.forEach(orgnummer -> asyncMap.put(orgnummer, getOrganisasjon(orgnummer, miljo, executor)));

        return asyncMap.getMap();
    }

}
