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
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.Set;

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

    @Timed(name = "providers", tags = {"operation", "pen_opprettPerson"})
    public PensjonforvalterResponse opprettPerson(OpprettPersonRequest opprettPersonRequest) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new OpprettPersonCommand(webClient, token.getTokenValue(), opprettPersonRequest).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreInntekt"})
    public PensjonforvalterResponse lagreInntekt(LagreInntektRequest lagreInntektRequest) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new LagreInntektCommand(webClient, token.getTokenValue(), lagreInntektRequest).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pen_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new GetInntekterCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpForhold"})
    public PensjonforvalterResponse lagreTpForhold(LagreTpForholdRequest lagreTpForholdRequest) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new LagreTpForholdCommand(webClient, token.getTokenValue(), lagreTpForholdRequest).call())
                .block();
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
    public PensjonforvalterResponse lagreTpYtelse(LagreTpYtelseRequest lagreTpYtelseRequest) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new LagreTpYtelseCommand(webClient, token.getTokenValue(), lagreTpYtelseRequest).call())
                .block();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    public Map<String, Object> checkStatus() {
        var statusMap =  CheckAliveUtil.checkConsumerStatus(
                serviceProperties.getUrl() + "/internal/isAlive",
                serviceProperties.getUrl() + "/internal/isReady",
                WebClient.builder().build());
        statusMap.put("team", "Dolly");

        var pensjonStatus = CheckAliveUtil.checkConsumerStatus(
                "https://pensjon-testdata-facade.dev.intern.nav.no/isAlive",
                "https://pensjon-testdata-facade.dev.intern.nav.no/isReady",
                WebClient.builder().build());
        pensjonStatus.put("team", "Pensjon");

        return Map.of(
                "pensjonforvalter", statusMap,
                "pensjon-testdata", pensjonStatus
        );
    }
}
