package no.nav.dolly.bestilling.skjermingsregister;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterDeleteCommand;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterGetCommand;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterPostCommand;
import no.nav.dolly.bestilling.skjermingsregister.command.SkjermingsregisterPutCommand;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.config.credentials.SkjermingsregisterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class SkjermingsRegisterConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public SkjermingsRegisterConsumer(TokenExchange tokenService,
                                      SkjermingsregisterProxyProperties serverProperties,
                                      ObjectMapper objectMapper,
                                      ExchangeFilterFunction metricsWebClientFilterFunction) {

        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .filter(metricsWebClientFilterFunction)
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "skjermingsdata-hent"})
    public SkjermingsDataResponse getSkjerming(String ident) {

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
    public void oppdaterPerson(SkjermingsDataRequest skjerming) {

        tokenService.exchange(serviceProperties)
                .flatMap(token -> new SkjermingsregisterGetCommand(webClient, skjerming.getPersonident(), token.getTokenValue()).call()
                        .flatMap(response -> {
                            if (response.isEksistererIkke()) {
                                return new SkjermingsregisterPostCommand(webClient, List.of(skjerming),
                                        token.getTokenValue()).call()
                                        .map(status -> {
                                            log.info("Opprettet skjerming på ident {} fraDato {} tilDato {}", status[0].getPersonident(), status[0].getSkjermetFra(), status[0].getSkjermetTil());
                                            return status;
                                        });
                            } else {
                                var status = new SkjermingsregisterPutCommand(webClient, skjerming.getPersonident(),
                                        skjerming.getSkjermetTil(), token.getTokenValue()).call();
                                log.info("Oppdatert skjerming for ident {}, ny tilDato {}", skjerming.getPersonident(), skjerming.getSkjermetTil());
                                return status;
                            }
                        }))
                .block();
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
