package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import no.nav.registre.orgnrservice.config.credentials.OrganisasjonServiceProperties;
import no.nav.registre.orgnrservice.consumer.exceptions.OrganisasjonApiException;
import no.nav.testnav.libs.commands.organisasjonservice.v1.GetOrganisasjonCommand;
import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@Slf4j
@Component
public class OrganisasjonApiConsumer {

    private final WebClient webClient;
    private final TokenExchange tokenExchange;
    private final OrganisasjonServiceProperties serviceProperties;
    private final ExecutorService executorService;
    private final MiljoerConsumer miljoerConsumer;

    OrganisasjonApiConsumer(
            OrganisasjonServiceProperties serviceProperties,
            TokenExchange tokenExchange,
            MiljoerConsumer miljoerConsumer
    ) {
        this.serviceProperties = serviceProperties;
        this.tokenExchange = tokenExchange;
        this.executorService = Executors.newFixedThreadPool(serviceProperties.getThreads());
        this.miljoerConsumer = miljoerConsumer;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    CompletableFuture<OrganisasjonDTO> getOrgnrFraMiljoeThreads(String orgnummer, String miljoe, String token) {
        return CompletableFuture.supplyAsync(
                () -> new GetOrganisasjonCommand(webClient, token, orgnummer, miljoe).call(),
                executorService
        );
    }

    public boolean finnesOrgnrIEreg(String orgnummer) {

        List<String> miljoer = miljoerConsumer.hentMiljoer().getEnvironments();
        Set<String> ekskluderteMiljoer = Set.of("qx", "u5");

        var token = tokenExchange.exchange(serviceProperties).block().getTokenValue();

        var futures = miljoer.stream()
                .filter(miljoe -> !ekskluderteMiljoer.contains(miljoe))
                .map(enkeltmiljoe -> getOrgnrFraMiljoeThreads(orgnummer, enkeltmiljoe, token))
                .collect(Collectors.toList());

        List<OrganisasjonDTO> miljoeListe = new ArrayList<>();
        for (CompletableFuture<OrganisasjonDTO> future : futures) {
            try {
                OrganisasjonDTO e = future.get();
                miljoeListe.add(e);
            } catch (Exception e) {
                throw new OrganisasjonApiException("Klarte ikke hente organisasjon fra et miljø", e);
            }
        }
        long antallMiljoerOrgnrFinnes = miljoeListe.stream().filter(Objects::nonNull).count();
        return antallMiljoerOrgnrFinnes > 0;
    }
}
