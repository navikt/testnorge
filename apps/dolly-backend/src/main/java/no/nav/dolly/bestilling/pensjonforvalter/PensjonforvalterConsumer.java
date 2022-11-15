package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetInntekterCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetMiljoerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.GetTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreInntektCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreTpYtelseCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.OpprettPersonCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.SletteTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
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
import java.util.concurrent.ConcurrentHashMap;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PensjonforvalterConsumer {

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
    public Set<String> getMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetMiljoerCommand(webClient, token.getTokenValue()).call())
                .block();
    }

    public Mono<AccessToken> getAccessToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettPerson"})
    public Flux<PensjonforvalterResponse> opprettPerson(OpprettPersonRequest opprettPersonRequest, Set<String> miljoer, AccessToken token) {

        opprettPersonRequest.setMiljoer(miljoer);
        log.info("Pensjon opprett person {}", opprettPersonRequest);
        return new OpprettPersonCommand(webClient, token.getTokenValue(), opprettPersonRequest).call();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreInntekt"})
    public Flux<PensjonforvalterResponse> lagreInntekt(LagreInntektRequest lagreInntektRequest, AccessToken token) {

        log.info("Pensjon lagre inntekt {}", lagreInntektRequest);
        return new LagreInntektCommand(webClient, token.getTokenValue(), lagreInntektRequest).call();
    }

    @Timed(name = "providers", tags = {"operation", "pen_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetInntekterCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpForhold"})
    public Flux<PensjonforvalterResponse> lagreTpForhold(LagreTpForholdRequest lagreTpForholdRequest, AccessToken token) {

        log.info("Pensjon lagre TP-forhold {}", lagreTpForholdRequest);
        return new LagreTpForholdCommand(webClient, token.getTokenValue(), lagreTpForholdRequest).call();
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

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpYtelse"})
    public Flux<PensjonforvalterResponse> lagreTpYtelse(LagreTpYtelseRequest lagreTpYtelseRequest, AccessToken token) {

        log.info("Pensjon lagre TP-ytelse {}", lagreTpYtelseRequest);
        return  new LagreTpYtelseCommand(webClient, token.getTokenValue(), lagreTpYtelseRequest).call();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    public Map<String, Object> checkStatus() {
        final String TEAM_DOLLY = "Team Dolly";

        var statusWebClient = WebClient.builder().build();

        var consumerStatus =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                statusWebClient);
        consumerStatus.put("team", TEAM_DOLLY);

        var status = new ConcurrentHashMap<String, Object>();
        status.put("pensjonforvalter", consumerStatus);

        try {
            Map response = statusWebClient.get()
                    .uri(serviceProperties.getUrl() + "/internal/status")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .block();
            status.putAll(response);
        } catch (Exception e) {
            log.warn("Feil med henting status fra " + serviceProperties.getUrl() + " med feil: " + e.getMessage(), e);
        }

        return status;
    }
}
