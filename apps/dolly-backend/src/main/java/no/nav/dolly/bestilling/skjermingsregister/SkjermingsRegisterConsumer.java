package no.nav.dolly.bestilling.skjermingsregister;

import tools.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
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
import java.time.LocalDateTime;
import java.util.List;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.apache.commons.lang3.StringUtils.isBlank;

@Slf4j
@Service
public class SkjermingsRegisterConsumer extends ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serverProperties;

    public SkjermingsRegisterConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient webClient) {

        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavDollyProxy();
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-slett"})
    public Mono<List<SkjermingDataResponse>> deleteSkjerming(List<String> identer) {

        return tokenService
                .exchange(serverProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(100))
                        .map(index -> new SkjermingsregisterPutCommand(webClient,
                                SkjermingDataRequest.builder()
                                        .personident(identer.get(index))
                                        .skjermetTil(LocalDateTime.now())
                                        .build(),
                                token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-oppdater"})
    public Mono<SkjermingDataResponse> oppdaterPerson(SkjermingDataRequest skjerming) {

        log.info("Sender forespørsel om skjerming for ident {}: {}", skjerming.getPersonident(), skjerming);
        return tokenService.exchange(serverProperties)
                .flatMap(token -> new SkjermingsregisterGetCommand(webClient, skjerming.getPersonident(), token.getTokenValue()).call()
                        .flatMap(response -> {
                            if (isBlank(response.getError())) {
                                if (response.isEksistererIkke()) {
                                    return new SkjermingsregisterPostCommand(webClient, skjerming,
                                            token.getTokenValue()).call()
                                            .collectList()
                                            .map(status -> {
                                                log.info("Opprettet skjerming på ident {} fraDato {} tilDato {}",
                                                        status.getFirst().getPersonident(), status.getFirst().getSkjermetFra(), status.getFirst().getSkjermetTil());
                                                return status.getFirst();
                                            });
                                } else {
                                    return nonNull(skjerming.getSkjermetTil()) ?

                                            new SkjermingsregisterPutCommand(webClient, skjerming, token.getTokenValue())
                                                    .call()
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
        return "testnav-dolly-proxy";
    }

}
