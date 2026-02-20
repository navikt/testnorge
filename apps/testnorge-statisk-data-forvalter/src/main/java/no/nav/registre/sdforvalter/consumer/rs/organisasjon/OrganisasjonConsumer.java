package no.nav.registre.sdforvalter.consumer.rs.organisasjon;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.config.Consumers;
import no.nav.registre.sdforvalter.domain.status.ereg.Organisasjon;
import no.nav.testnav.libs.commands.organisasjonservice.v1.GetOrganisasjonCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.reactivesecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Slf4j
@Component
public class OrganisasjonConsumer {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenExchange;
    private final Executor executor;

    public OrganisasjonConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        serverProperties = consumers.getTestnavOrganisasjonService();
        this.tokenExchange = tokenExchange;
        this.executor = Executors.newFixedThreadPool(serverProperties.getThreads());
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    private CompletableFuture<Organisasjon> getOrganisasjon(String orgnummer, String miljo, Executor executor) {
        var accessToken = tokenExchange.exchange(serverProperties).block();
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
