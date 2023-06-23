package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterDeleteCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterGetBrukerCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterGetMiljoeCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaGetCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaforvalterPostAap;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaforvalterPostAap115;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaforvalterPostArenaBruker;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaforvalterPostArenadagpenger;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Request;
import no.nav.dolly.bestilling.arenaforvalter.dto.Aap115Response;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapRequest;
import no.nav.dolly.bestilling.arenaforvalter.dto.AapResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaResponse;
import no.nav.dolly.bestilling.arenaforvalter.dto.InaktiverResponse;
import no.nav.dolly.config.credentials.ArenaforvalterProxyProperties;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class ArenaForvalterConsumer implements ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenService;

    public ArenaForvalterConsumer(
            ArenaforvalterProxyProperties serverProperties,
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

    @Timed(name = "providers", tags = {"operation", "arena_getIdent"})
    public Flux<ArenaArbeidssokerBruker> getBruker(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaForvalterGetBrukerCommand(webClient, ident, miljoe, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Hentet ident {} fra Arenaforvalter {}", ident, response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_deleteIdent"})
    public Flux<InaktiverResponse> deleteIdenter(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call()
                        .flatMap(miljoe -> Flux.fromIterable(identer)
                                .delayElements(Duration.ofMillis(100))
                                .flatMap(ident -> new ArenaForvalterDeleteCommand(webClient, ident, miljoe,
                                        token.getTokenValue()).call())));
    }

    @Timed(name = "providers", tags = {"operation", "arena_deleteIdent"})
    public Mono<InaktiverResponse> inaktiverBruker(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new ArenaForvalterDeleteCommand(webClient, ident, miljoe, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Inaktivert bruker {} mot Arenaforvalter {}", ident, response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postBruker"})
    public Flux<ArenaNyeBrukereResponse> postArenaBruker(ArenaNyeBrukere arenaNyeBrukere) {

        log.info("Arena opprett bruker {}", arenaNyeBrukere);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaforvalterPostArenaBruker(webClient, arenaNyeBrukere, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Opprettet bruker {} mot Arenaforvalter {}",
                        arenaNyeBrukere.getNyeBrukere().get(0).getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postAap"})
    public Flux<AapResponse> postAap(AapRequest aapRequest) {

        log.info("Arena opprett Aap {}", aapRequest);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaforvalterPostAap(webClient, aapRequest, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Opprettet aap {} mot Arenaforvalter {}",
                        aapRequest.getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postAap115"})
    public Flux<Aap115Response> postAap115(Aap115Request aap115Request) {

        log.info("Arena opprett Aap115 {}", aap115Request);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaforvalterPostAap115(webClient, aap115Request, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Opprettet aap115 {} mot Arenaforvalter {}",
                        aap115Request.getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postDagpenger"})
    public Flux<ArenaNyeDagpengerResponse> postArenaDagpenger(ArenaDagpenger arenaDagpenger) {

        log.info("Opprett dagpenger mot Arenaforvalter {}", arenaDagpenger);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaforvalterPostArenadagpenger(webClient, arenaDagpenger, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Opprettet dagpenger for {} mot Arenaforvalter {}",
                        arenaDagpenger.getPersonident(), arenaDagpenger)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_getEnvironments"})
    public Flux<String> getEnvironments() {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "arena_getEnvironments"})
    public Mono<ArenaResponse> getArenaBruker(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new ArenaGetCommand(webClient, ident, miljoe, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Hentet bruker {} fra Arena miljoe {} {}", ident, miljoe, response));
    }

    @Override
    public String serviceUrl() {
        return serviceProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-arena-forvalteren-proxy";
    }

}
