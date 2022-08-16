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
import no.nav.dolly.exceptions.DollyFunctionalException;
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

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
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
        try {
            return tokenService.exchange(serviceProperties)
                    .flatMap(token -> new GetMiljoerCommand(webClient, token.getTokenValue()).call())
                    .block();

        } catch (RuntimeException e) {
            log.error("Feilet å lese tilgjengelige miljøer fra pensjon. {}", e.getMessage(), e);
            return emptySet();
        }
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettPerson"})
    public PensjonforvalterResponse opprettPerson(OpprettPersonRequest opprettPersonRequest) {

        var response = new OpprettPersonCommand(webClient, serviceProperties.getAccessToken(tokenService), opprettPersonRequest)
                .call()
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException("Klarte ikke å opprette person i pensjon-testdata-facade");
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreInntekt"})
    public PensjonforvalterResponse lagreInntekt(LagreInntektRequest lagreInntektRequest) {

        var response = new LagreInntektCommand(webClient, serviceProperties.getAccessToken(tokenService), lagreInntektRequest)
                .call()
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å lagre inntekt for %s i pensjon-testdata-facade", lagreInntektRequest.getFnr()));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        var response = new GetInntekterCommand(webClient, serviceProperties.getAccessToken(tokenService), ident, miljoe)
                .call()
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å hente inntekt for %s i %s fra pensjon-testdata-facade", ident, miljoe));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpForhold"})
    public PensjonforvalterResponse lagreTpForhold(LagreTpForholdRequest lagreTpForholdRequest) {

        var response = new LagreTpForholdCommand(webClient, serviceProperties.getAccessToken(tokenService), lagreTpForholdRequest)
                .call()
                .block();

        if (isNull(response) || isNull(response.getBody()) || !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å lagre TP forhold for %s i PESYS (pensjon)", lagreTpForholdRequest.getFnr()));
        }

        if (isNull(response.getBody().getStatus()) || response.getBody().getStatus().isEmpty()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å få TP forhold respons for %s i PESYS (pensjon)", lagreTpForholdRequest.getFnr()));
        }

        return response.getBody();
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

        var response = new GetTpForholdCommand(webClient, serviceProperties.getAccessToken(tokenService), ident, miljoe)
                .call()
                .block();

        if (isNull(response) || !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å hente TP forhold for %s i %s fra TP (pensjon)", ident, miljoe));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpYtelse"})
    public PensjonforvalterResponse lagreTpYtelse(LagreTpYtelseRequest lagreTpYtelseRequest) {
        var response = new LagreTpYtelseCommand(webClient, serviceProperties.getAccessToken(tokenService), lagreTpYtelseRequest)
                .call()
                .block();

        if (isNull(response) || isNull(response.getBody()) || !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Feilet å lagre TP-ytelse for %s i PESYS (pensjon)", lagreTpYtelseRequest.getFnr()));
        }

        if (isNull(response.getBody().getStatus()) || response.getBody().getStatus().isEmpty()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å få TP-ytelse respons for %s i PESYS (pensjon)", lagreTpYtelseRequest.getFnr()));
        }

        return response.getBody();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

}
