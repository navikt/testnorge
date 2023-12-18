package no.nav.dolly.bestilling.skjermingsregister;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterDeleteCommand;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterGetCommand;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterPostCommand;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterPutCommand;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingDataResponse;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class SkjermingsRegisterConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public SkjermingsRegisterConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavSkjermingsregisterProxy();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-slett"})
    public Mono<List<Void>> deleteSkjerming(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(100))
                        .map(index -> new SkjermingsregisterDeleteCommand(webClient,
                                identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-oppdater"})
    public Mono<SkjermingDataResponse> oppdaterPerson(SkjermingDataRequest skjerming) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SkjermingsregisterGetCommand(webClient, skjerming.getPersonident(), token.getTokenValue()).call()
                        .flatMap(response -> {
                            if (isBlank(response.getError())) {
                                if (response.isEksistererIkke()) {
                                    return new SkjermingsregisterPostCommand(webClient, List.of(skjerming),
                                            token.getTokenValue()).call()
                                            .collectList()
                                            .map(status -> {
                                                log.info("Opprettet skjerming på ident {} fraDato {} tilDato {}",
                                                        status.get(0).getPersonident(), status.get(0).getSkjermetFra(), status.get(0).getSkjermetTil());
                                                return status.get(0);
                                            });
                                } else {
                                    return nonNull(skjerming.getSkjermetTil()) ?

                                            new SkjermingsregisterPutCommand(webClient, skjerming.getPersonident(),
                                                    skjerming.getSkjermetTil(), token.getTokenValue()).call()
                                                    .map(status -> {
                                                        log.info("Oppdatert skjerming for ident {}, ny tilDato {}", skjerming.getPersonident(), skjerming.getSkjermetTil());
                                                        return status;
                                                    }) :

                                            Mono.just(new SkjermingDataResponse());
                                }
                            } else {
                                return Mono.just(response);
                            }
                        }));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-skjermingsregister-proxy";
    }

}
