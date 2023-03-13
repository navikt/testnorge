package no.nav.dolly.bestilling.arbeidsplassencv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenDeleteCVCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenGetCVCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenPostPersonCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenPostSamtykkeCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenPutCVCommand;
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
public class ArbeidsplassenCVConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenService;

    public ArbeidsplassenCVConsumer(
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
    public Flux<ArbeidsplassenCVStatusDTO> hentCV(String ident) {

        log.info("Henter CV på ident: {} fra arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenGetCVCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Hentet CV for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_getCV" })
    public Flux<ArbeidsplassenCVStatusDTO> hentPerson(String ident) {

        log.info("Henter person på ident: {} fra arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenGetCVCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Hentet person for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_putCV" })
    public Flux<ArbeidsplassenCVStatusDTO> oppdaterCV(String ident, ArbeidsplassenCVDTO arbeidsplassenCV) {

        log.info("Oppdaterer CV på ident: {} til arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenPutCVCommand(webClient, ident, arbeidsplassenCV, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Oppdatert CV for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_putCV" })
    public Flux<ArbeidsplassenCVStatusDTO> opprettSamtykke(String ident) {

        log.info("Oppretter samtykke på ident: {} til arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenPostSamtykkeCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Opprettet samtykke for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_putCV" })
    public Flux<ArbeidsplassenCVStatusDTO> opprettPerson(String ident) {

        log.info("Oppretter person på ident: {} til arbeidsplassenCV", ident);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenPostPersonCommand(webClient, ident, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Opprettet person for ident {} {}", ident, resultat));
    }


    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_deleteCV" })
    public Flux<ArbeidsplassenCVDTO> deleteCV(String ident) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenDeleteCVCommand(webClient, ident, null, token.getTokenValue()).call());
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
                        .flatMap(ident -> new ArbeidsplassenDeleteCVCommand(webClient, ident, null,
                                token.getTokenValue()).call()));
    }
}
