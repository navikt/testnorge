package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterDeleteCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterGetBrukerCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaForvalterGetMiljoeCommand;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaforvalterPostArenadagpenger;
import no.nav.dolly.bestilling.arenaforvalter.command.ArenaforvalterPostArenadata;
import no.nav.dolly.config.credentials.ArenaforvalterProxyProperties;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaDagpenger;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeDagpengerResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Component
@Slf4j
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
    public Flux<ArenaArbeidssokerBruker> getBruker(String ident, String miljoe, AccessToken token) {

        return new ArenaForvalterGetBrukerCommand(webClient, ident, miljoe, token.getTokenValue()).call()
                .doOnNext(response -> log.info("Hentet ident {} fra Arenaforvalter {}", ident, response));
    }

    @Timed(name = "providers", tags = {"operation", "arena_deleteIdent"})
    public Flux<String> deleteIdenter(List<String> identer) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call()
                        .flatMap(miljoe -> Flux.fromIterable(identer)
                                .delayElements(Duration.ofMillis(100))
                                .flatMap(ident -> new ArenaForvalterDeleteCommand(webClient, ident, miljoe,
                                        token.getTokenValue()).call())));
    }

    @Timed(name = "providers", tags = {"operation", "arena_deleteIdent"})
    public Flux<String> deleteIdent(String ident, String miljoe, AccessToken token) {

        return new ArenaForvalterDeleteCommand(webClient, ident, miljoe, token.getTokenValue()).call();
    }

    public Mono<AccessToken> getToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "arena_postBruker"})
    public Flux<ArenaNyeBrukereResponse> postArenadata(ArenaNyeBrukere arenaNyeBrukere, AccessToken accessToken) {

        log.info("Arena opprett bruker {}", arenaNyeBrukere);
        return new ArenaforvalterPostArenadata(webClient, arenaNyeBrukere, accessToken.getTokenValue()).call()
                .doOnNext(response -> log.info("Opprettet bruker {} mot Arenaforvalter {}", response));
    }

    @Timed(name = "providers", tags = {"operation", "arena_postDagpenger"})
    public Flux<ArenaNyeDagpengerResponse> postArenaDagpenger(ArenaDagpenger arenaDagpenger, AccessToken accessToken) {

        log.info("Opprett dagpenger mot Arenaforvalter {}", arenaDagpenger);
        return new ArenaforvalterPostArenadagpenger(webClient, arenaDagpenger, accessToken.getTokenValue()).call()
                .doOnNext(response -> log.info("Opprettet dagpenger for {} mot Arenaforvalter {}",
                        arenaDagpenger.getPersonident(), arenaDagpenger));
    }

    @Timed(name = "providers", tags = {"operation", "arena_getEnvironments"})
    public Flux<String> getEnvironments(AccessToken token) {

        return new ArenaForvalterGetMiljoeCommand(webClient, token.getTokenValue()).call();
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
