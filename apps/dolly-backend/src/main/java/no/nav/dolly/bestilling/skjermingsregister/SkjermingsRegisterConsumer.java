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
import no.nav.dolly.config.credentials.SkjermingsregisterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static java.util.Objects.nonNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class SkjermingsRegisterConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public SkjermingsRegisterConsumer(TokenExchange tokenService,
                                      SkjermingsregisterProxyProperties serverProperties,
                                      ObjectMapper objectMapper) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-hent"})
    public SkjermingDataResponse getSkjerming(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SkjermingsregisterGetCommand(webClient, ident, token.getTokenValue()).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-slett"})
    public Mono<List<Void>> deleteSkjerming(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size())
                        .delayElements(Duration.ofMillis(100))
                        .map(index -> new SkjermingsregisterDeleteCommand(webClient,
                                identer.get(index), token.getTokenValue()).call())
                        .flatMap(Flux::from))
                .collectList();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-oppdater"})
    public Mono<SkjermingDataResponse> oppdaterPerson(SkjermingDataRequest skjerming) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new SkjermingsregisterGetCommand(webClient, skjerming.getPersonident(), token.getTokenValue()).call()
                        .flatMap(response -> {
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
                        }));
    }

    public Map<String, String> checkAlive() {

        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-skjermingsregister-proxy";
    }

}
