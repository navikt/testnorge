package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterDeleteCommand;
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
import no.nav.dolly.bestilling.arenaforvalter.dto.ArenaStatusResponse;
import no.nav.dolly.config.Consumers;
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
import java.time.LocalDate;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class ArenaForvalterConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenService;

    public ArenaForvalterConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient webClient) {

        serverProperties = consumers.getTestnavArenaForvalterenProxy();
        this.tokenService = tokenService;
        this.webClient = webClient
                .mutate()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "arena_deleteIdent"})
    public Flux<ArenaResponse> deleteIdenter(List<String> identer) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call()
                        .flatMap(miljoe -> Flux.fromIterable(identer)
                                .delayElements(Duration.ofMillis(100))
                                .flatMap(ident -> new ArenaForvalterDeleteCommand(webClient, ident, null, miljoe,
                                        token.getTokenValue()).call())));
    }

    @Timed(name = "providers", tags = {"operation", "arena_deleteIdent"})
    public Mono<ArenaResponse> inaktiverBruker(String ident, LocalDate stansdato, String miljoe) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new ArenaForvalterDeleteCommand(webClient, ident, stansdato, miljoe, token.getTokenValue()).call()
                        .doOnNext(response -> log.info("Inaktivert bruker {} mot Arenaforvalter {}", ident, response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postBruker"})
    public Flux<ArenaNyeBrukereResponse> postArenaBruker(ArenaNyeBrukere arenaNyeBrukere) {

        log.info("Arena opprett bruker {}", arenaNyeBrukere);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new ArenaforvalterPostArenaBruker(webClient, arenaNyeBrukere, token.getTokenValue()).call()
                        .doOnNext(response -> log.info("Opprettet bruker {} mot Arenaforvalter {}",
                                arenaNyeBrukere.getNyeBrukere().getFirst().getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postAap"})
    public Flux<AapResponse> postAap(AapRequest aapRequest) {

        log.info("Arena opprett Aap {}", aapRequest);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new ArenaforvalterPostAap(webClient, aapRequest, token.getTokenValue()).call()
                        .doOnNext(response -> log.info("Opprettet aap {} mot Arenaforvalter {}",
                                aapRequest.getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postAap115"})
    public Flux<Aap115Response> postAap115(Aap115Request aap115Request) {

        log.info("Arena opprett Aap115 {}", aap115Request);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new ArenaforvalterPostAap115(webClient, aap115Request, token.getTokenValue()).call()
                        .doOnNext(response -> log.info("Opprettet aap115 {} mot Arenaforvalter {}",
                                aap115Request.getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postDagpenger"})
    public Flux<ArenaNyeDagpengerResponse> postArenaDagpenger(ArenaDagpenger arenaDagpenger) {

        log.info("Opprett dagpenger mot Arenaforvalter {}", arenaDagpenger);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new ArenaforvalterPostArenadagpenger(webClient, arenaDagpenger, token.getTokenValue()).call()
                        .doOnNext(response -> log.info("Opprettet dagpenger for {} mot Arenaforvalter {}",
                                arenaDagpenger.getPersonident(), response)));
    }

    @Timed(name = "providers", tags = {"operation", "arena_getEnvironments"})
    public Flux<String> getEnvironments() {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "arena_getEnvironments"})
    public Mono<ArenaStatusResponse> getArenaBruker(String ident, String miljoe) {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new ArenaGetCommand(webClient, ident, miljoe, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Hentet bruker {} fra Arena miljoe {} {}", ident, miljoe, response));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-arena-forvalteren-proxy";
    }

}
