package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetMiljoerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetPoppInntekterCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreAlderspensjonCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagrePoppInntektCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreTpYtelseCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.OpprettPersonCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.SletteTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.config.credentials.PensjonforvalterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PensjonforvalterConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PensjonforvalterConsumer(TokenExchange tokenService,
                                    PensjonforvalterProxyProperties serverProperties,
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

    @Timed(name = "providers", tags = {"operation", "pen_getMiljoer"})
    public Mono<Set<String>> getMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetMiljoerCommand(webClient, token.getTokenValue()).call());
    }

    public Mono<AccessToken> getAccessToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "popp_lagreInntekt"})
    public Flux<PensjonforvalterResponse> lagreInntekter(PensjonPoppInntektRequest pensjonPoppInntektRequest,
                                                         Set<String> miljoer, AccessToken token) {

        log.info("Popp lagre inntekt {}", pensjonPoppInntektRequest);
        return Flux.fromIterable(miljoer)
                .flatMap(miljoe -> new LagrePoppInntektCommand(webClient, token.getTokenValue(),
                        pensjonPoppInntektRequest, miljoe).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettPerson"})
    public Flux<PensjonforvalterResponse> opprettPerson(PensjonPersonRequest pensjonPersonRequest,
                                                        Set<String> miljoer, AccessToken token) {

        pensjonPersonRequest.setMiljoer(miljoer);
        log.info("Pensjon opprett person {}", pensjonPersonRequest);
        return new OpprettPersonCommand(webClient, token.getTokenValue(), pensjonPersonRequest).call();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreAlderspensjon"})
    public Flux<PensjonforvalterResponse> lagreAlderspensjon(AlderspensjonRequest request, AccessToken token) {

        log.info("Pensjon lagre alderspensjon {}", request);
        return new LagreAlderspensjonCommand(webClient, token.getTokenValue(), request).call();
    }

    @Timed(name = "providers", tags = {"operation", "pen_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetPoppInntekterCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreTpForhold" })
    public Flux<PensjonforvalterResponse> lagreTpForhold(PensjonTpForholdRequest pensjonTpForholdRequest, AccessToken token) {

        log.info("Pensjon lagre TP-forhold {}", pensjonTpForholdRequest);
        return new LagreTpForholdCommand(webClient, token.getTokenValue(), pensjonTpForholdRequest).call();
    }

    @Timed(name = "providers", tags = {"operation", "pen_sletteTpForhold"})
    public void sletteTpForhold(List<String> identer) {

        tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new GetMiljoerCommand(webClient, token.getTokenValue()).call()
                        .flatMapMany(miljoer -> Flux.range(0, identer.size())
                                .map(index -> new SletteTpForholdCommand(webClient, identer.get(index), miljoer, token.getTokenValue()).call())))
                .flatMap((Flux::from))
                .collectList()
                .subscribe(response -> log.info("Slettet mot PESYS (tp) i alle miljoer"));
    }

    @Timed(name = "providers", tags = {"operation", "pen_getTpForhold"})
    public JsonNode getTpForhold(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetTpForholdCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreTpYtelse" })
    public Flux<PensjonforvalterResponse> lagreTpYtelse(PensjonTpYtelseRequest pensjonTpYtelseRequest, AccessToken token) {

        log.info("Pensjon lagre TP-ytelse {}", pensjonTpYtelseRequest);
        return new LagreTpYtelseCommand(webClient, token.getTokenValue(), pensjonTpYtelseRequest).call();
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
        return "testnav-pensjon-testdata-facade-proxy";
    }

}
