package no.nav.dolly.bestilling.personservice;

import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.personservice.command.PdlPersonerGetCommand;
import no.nav.dolly.bestilling.personservice.command.PersonServiceExistCommand;
import no.nav.dolly.bestilling.personservice.dto.PersonServiceResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.Objects.isNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Service
@Slf4j
public class PersonServiceConsumer extends ConsumerStatus {

    private static final int BLOCK_SIZE = 100;
    private static final int MAX_RETRIES = 3;

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public PersonServiceConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            WebClient webClient,
            ObjectMapper objectMapper) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavPersonService();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "personService_isPerson"})
    public Mono<PersonServiceResponse> isPerson(String ident, Set<String> opplysningId) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new PersonServiceExistCommand(webClient, ident, opplysningId, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pdl_getPersoner"})
    public Flux<PdlPersonBolk> getPdlPersoner(List<String> identer) {

        return getPdlPersoner(identer, new AtomicInteger(0));
    }

    @Timed(name = "providers", tags = {"operation", "pdl_getPersoner"})
    public Flux<PdlPersonBolk> getPdlPersonerNoRetries(List<String> identer) {

        return getPdlPersoner(identer, new AtomicInteger(MAX_RETRIES));
    }

    @Timed(name = "providers", tags = {"operation", "pdl_getPersoner"})
    public Flux<PdlPersonBolk> getPdlPersoner(List<String> identer, AtomicInteger retry) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / BLOCK_SIZE + 1)
                        .flatMap(index -> new PdlPersonerGetCommand(webClient,
                                identer.subList(index * BLOCK_SIZE, Math.min((index + 1) * BLOCK_SIZE, identer.size())),
                                token.getTokenValue()
                        ).call()))

                .flatMap(resultat -> {

                    if (retry.get() < MAX_RETRIES &&
                            (isNull(resultat.getData()) || resultat.getData().getHentPersonBolk().stream()
                                    .anyMatch(data -> isNull(data.getPerson())))) {

                        return Flux.just(true)
                                .doOnNext(melding ->
                                        log.info("PDL har tomt resultat for {}, retry #{}", String.join(", ", identer),
                                                retry.incrementAndGet()))
                                .delayElements(Duration.ofMillis(200))
                                .flatMap(delay -> getPdlPersoner(identer, new AtomicInteger(retry.get())));
                    } else {
                        return Flux.just(resultat);
                    }
                });
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-person-service";
    }
}
