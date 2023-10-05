package no.nav.dolly.bestilling.arbeidsplassencv;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenDeleteCVCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenGodtaHjemmelCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenGodtaVilkaarCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenPostPersonCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.command.ArbeidsplassenPutCVCommand;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.PAMCVDTO;
import no.nav.dolly.config.credentials.ArbeidsplassenProxyProperties;
import no.nav.dolly.metrics.Timed;
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

    public static final String ARBEIDSPLASSEN_CALL_ID = "Nav-CallId";
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

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_oppdaterCV" })
    public Flux<ArbeidsplassenCVStatusDTO> oppdaterCV(String ident, PAMCVDTO arbeidsplassenCV, String uuid) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenPutCVCommand(webClient, ident, arbeidsplassenCV, uuid, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Oppdatert CV for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_godtaVilkaar" })
    public Flux<ArbeidsplassenCVStatusDTO> godtaVilkaar(String ident, String uuid) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenGodtaVilkaarCommand(webClient, ident, uuid, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Opprettet vilkaar for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_godtaHjemmel" })
    public Flux<ArbeidsplassenCVStatusDTO> godtaHjemmel(String ident, String uuid) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenGodtaHjemmelCommand(webClient, ident, uuid, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Opprettet hjemmel for ident {} {}", ident, resultat));
    }

    @Timed(name = "providers", tags = { "operation", "arbeidsplassen_opprettPerson" })
    public Flux<ArbeidsplassenCVStatusDTO> opprettPerson(String ident, String uuid) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArbeidsplassenPostPersonCommand(webClient, ident, uuid, token.getTokenValue()).call())
                .doOnNext(resultat -> log.info("Opprettet person for ident {} {}", ident, resultat));
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

    public Flux<ArbeidsplassenCVStatusDTO> deleteCVer(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .flatMap(ident -> new ArbeidsplassenDeleteCVCommand(webClient, ident,
                                token.getTokenValue()).call()));
    }
}
