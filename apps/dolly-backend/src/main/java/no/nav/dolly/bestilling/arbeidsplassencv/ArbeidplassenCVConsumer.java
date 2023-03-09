package no.nav.dolly.bestilling.arbeidsplassencv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenDeleteCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenGetCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenPutCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.dolly.config.credentials.ArbeidsplassenProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.arbeidsplassencv.v1.ArbeidsplassenCVDTO;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Component
@Slf4j
public class ArbeidplassenCVConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenService;

    public ArbeidplassenCVConsumer(
            ArbeidsplassenProxyProperties serverProperties,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_getCV" })
    public Flux<ArbeidsplassenCVDTO> hentCV(String ident) {

        log.info("Henter CV på ident: {} fra arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenGetCommand(webClient, ident, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_putCV" })
    public Flux<ArbeidsplassenCVStatusDTO> oppdaterCV(String ident, ArbeidsplassenCVDTO arbeidsplassenCV) {

        log.info("Oppdaterer CV på ident: {} til arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenPutCommand(webClient, ident, arbeidsplassenCV, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_deleteCV" })
    public Flux<ArbeidsplassenCVDTO> deleteCV(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenDeleteCommand(webClient, ident, null, token.getTokenValue()).call());
    }

    public Mono<AccessToken> getToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-arbeidsplassencv-proxy";
    }

    public Flux<ArbeidsplassenCVDTO> deleteCVer(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .flatMap(ident -> new ArbeidsplassenDeleteCommand(webClient, ident, null,
                                token.getTokenValue()).call()));
    }
}
