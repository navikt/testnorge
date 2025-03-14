package no.nav.registre.orgnrservice.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orgnrservice.config.Consumers;
import no.nav.registre.orgnrservice.consumer.command.GetOrganisasjonCommand;
import no.nav.registre.orgnrservice.consumer.command.MiljoerCommand;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.util.Objects.nonNull;

@Slf4j
@Component
public class OrganisasjonConsumer {

    private static final List<String> EXCLUDE_ENVS = List.of("qx");

    private final WebClient organisasjonWebClient;
    private final WebClient miljoerWebClient;
    private final TokenExchange tokenExchange;
    private final ServerProperties miljoerServerProperties;
    private final ServerProperties organisasjonServerProperties;

    public OrganisasjonConsumer(
            Consumers consumers,
            TokenExchange tokenExchange,
            WebClient webClient
    ) {
        miljoerServerProperties = consumers.getTestnavMiljoerService();
        organisasjonServerProperties = consumers.getTestnavOrganisasjonService();
        this.tokenExchange = tokenExchange;
        this.organisasjonWebClient = webClient
                .mutate()
                .baseUrl(organisasjonServerProperties.getUrl())
                .build();
        this.miljoerWebClient = webClient
                .mutate()
                .baseUrl(miljoerServerProperties.getUrl())
                .build();
    }

    private static boolean supportedEnv(String miljoe) {

        return !EXCLUDE_ENVS.contains(miljoe);
    }

    public boolean finnesOrgnrIEreg(String orgnummer) {

        var organisasjoner =
                Mono.zip(tokenExchange.exchange(miljoerServerProperties),
                                tokenExchange.exchange(organisasjonServerProperties))
                        .flatMapMany(token -> new MiljoerCommand(miljoerWebClient, token.getT1().getTokenValue()).call()
                                .flatMapMany(miljoer -> Flux.fromIterable(Arrays.asList(miljoer))
                                        .filter(OrganisasjonConsumer::supportedEnv)
                                        .flatMap(miljoe ->
                                                new GetOrganisasjonCommand(organisasjonWebClient, token.getT2().getTokenValue(),
                                                        orgnummer, miljoe).call())))
                        .collectList()
                        .block();

        return nonNull(organisasjoner) && !organisasjoner.isEmpty();
    }
}