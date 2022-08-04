package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orgnrservice.config.credentials.MiljoerServiceProperties;
import no.nav.registre.orgnrservice.config.credentials.OrganisasjonServiceProperties;
import no.nav.registre.orgnrservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.orgnrservice.consumer.command.MiljoerCommand;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class OrganisasjonApiConsumer {

    private static final List<String> EXCLUDE_ENVS = List.of("qx");

    private final WebClient organisasjonWebClient;
    private final WebClient miljoerWebClient;
    private final TokenExchange tokenExchange;
    private final MiljoerServiceProperties miljoerServiceProperties;
    private final OrganisasjonServiceProperties organisasjonServiceProperties;

    public OrganisasjonApiConsumer(
            MiljoerServiceProperties miljoerServiceProperties,
            OrganisasjonServiceProperties organisasjonServiceProperties,
            TokenExchange tokenExchange,
            ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.miljoerServiceProperties = miljoerServiceProperties;
        this.organisasjonServiceProperties = organisasjonServiceProperties;
        this.tokenExchange = tokenExchange;
        this.organisasjonWebClient = WebClient.builder()
                .baseUrl(organisasjonServiceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
        this.miljoerWebClient = WebClient.builder()
                .baseUrl(miljoerServiceProperties.getUrl())
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    private static boolean supportedEnv(String miljoe) {

        return !EXCLUDE_ENVS.contains(miljoe);
    }

    public boolean finnesOrgnrIEreg(String orgnummer) {

        var organisasjoner =
                Mono.zip(tokenExchange.exchange(miljoerServiceProperties),
                                tokenExchange.exchange(organisasjonServiceProperties))
                        .flatMapMany(token -> new MiljoerCommand(miljoerWebClient, token.getT1().getTokenValue()).call()
                                .flatMapMany(miljoer -> Flux.fromIterable(Arrays.asList(miljoer))
                                        .filter(OrganisasjonApiConsumer::supportedEnv)
                                        .flatMap(miljoe ->
                                                new GetOrganisasjonCommand(organisasjonWebClient, token.getT2().getTokenValue(),
                                                        orgnummer, miljoe).call())))
                        .collectList()
                        .block();

        return nonNull(organisasjoner) && !organisasjoner.isEmpty();
    }
}