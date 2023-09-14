package no.nav.dolly.consumer.pdlperson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.consumer.pdlperson.command.PdlBolkPersonCommand;
import no.nav.dolly.consumer.pdlperson.command.PdlBolkPersonGetCommand;
import no.nav.dolly.consumer.pdlperson.command.PdlPersonGetCommand;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PdlPersonConsumer implements ConsumerStatus {

    private static final int BLOCK_SIZE = 100;
    private static final int MAX_RETRIES = 3;
    private final TokenExchange tokenService;
    private final ServerProperties serviceProperties;
    private final WebClient webClient;

    public PdlPersonConsumer(
            TokenExchange tokenService,
            PdlProxyProperties serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .clientConnector(new ReactorClientHttpConnector(
                        HttpClient.create(ConnectionProvider.builder("custom")
                                .maxConnections(10)
                                .pendingAcquireMaxCount(5000)
                                .pendingAcquireTimeout(Duration.ofMinutes(15))
                                .build())))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_getPerson"})
    public JsonNode getPdlPerson(String ident, PDL_MILJOER pdlMiljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap((AccessToken token) -> new PdlPersonGetCommand(webClient, ident, token.getTokenValue(), pdlMiljoe)
                        .call()).block();
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

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.range(0, identer.size() / BLOCK_SIZE + 1)
                        .flatMap(index -> new PdlBolkPersonGetCommand(webClient,
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

    @Timed(name = "providers", tags = {"operation", "pdl_getPersoner"})
    public Mono<JsonNode> getPdlPersonerJson(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new PdlBolkPersonCommand(webClient, identer, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-pdl-proxy";
    }

    public static String hentQueryResource(String pathResource) {
        val resource = new ClassPathResource(pathResource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", pathResource, e);
            return null;
        }
    }

    public enum PDL_MILJOER {
        Q1, Q2
    }

}
