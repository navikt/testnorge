package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.pensjonforvalter.command.*;
import no.nav.dolly.bestilling.pensjonforvalter.domain.*;
import no.nav.dolly.config.credentials.PensjonforvalterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Service
public class PensjonforvalterConsumer implements ConsumerStatus {

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final ServerProperties serviceProperties;

    public PensjonforvalterConsumer(
            TokenExchange tokenService,
            PensjonforvalterProxyProperties serverProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "pen_getMiljoer"})
    public Mono<Set<String>> getMiljoer() {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new HentMiljoerCommand(webClient, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "popp_lagreInntekt"})
    public Flux<PensjonforvalterResponse> lagreInntekter(PensjonPoppInntektRequest pensjonPoppInntektRequest) {

        log.info("Popp lagre inntekt {}", pensjonPoppInntektRequest);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new LagrePoppInntektCommand(webClient, token.getTokenValue(),
                                pensjonPoppInntektRequest).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettPerson"})
    public Flux<PensjonforvalterResponse> opprettPerson(PensjonPersonRequest pensjonPersonRequest,
                                                        Set<String> miljoer) {

        pensjonPersonRequest.setMiljoer(miljoer);
        log.info("Pensjon opprett person {}", pensjonPersonRequest);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new OpprettPersonCommand(webClient, pensjonPersonRequest, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_hentSamboer"})
    public Flux<PensjonSamboerResponse> hentSamboer(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new HentSamboerCommand(webClient, ident, miljoe, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Pensjon samboer for {} i {} hentet {}", ident, miljoe, response));
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettSamboer"})
    public Flux<PensjonforvalterResponse> lagreSamboer(PensjonSamboerRequest pensjonSamboerRequest,
                                                       String miljoe) {

        log.info("Pensjon samboer opprett i {} {}", miljoe, pensjonSamboerRequest);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new LagreSamboerCommand(webClient, pensjonSamboerRequest, miljoe, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettSamboer"})
    public Flux<PensjonforvalterResponse> annullerSamboer(String ident, String periodeId, String miljoe) {

        log.info("Pensjon samboer annuller {} periodeId {}", ident, periodeId);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new AnnullerSamboerCommand(webClient, periodeId, miljoe, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreAlderspensjon"})
    public Flux<PensjonforvalterResponse> lagreAlderspensjon(AlderspensjonRequest request) {

        log.info("Pensjon lagre alderspensjon {}", request);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new LagreAlderspensjonCommand(webClient, token.getTokenValue(), request).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreUforetrygd"})
    public Flux<PensjonforvalterResponse> lagreUforetrygd(PensjonUforetrygdRequest request) {

        log.info("Pensjon lagre uforetrygd {}", request);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new LagreUforetrygdCommand(webClient, token.getTokenValue(), request).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new HentPoppInntekterCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpForhold"})
    public Flux<PensjonforvalterResponse> lagreTpForhold(PensjonTpForholdRequest pensjonTpForholdRequest) {

        log.info("Pensjon lagre TP-forhold {}", pensjonTpForholdRequest);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new LagreTpForholdCommand(webClient, token.getTokenValue(), pensjonTpForholdRequest).call());
    }

    @Timed(name = "providers", tags = {"operation", "pen_sletteTpForhold"})
    public void sletteTpForhold(List<String> identer) {

        tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new HentMiljoerCommand(webClient, token.getTokenValue()).call()
                        .flatMapMany(miljoer -> Flux.range(0, identer.size())
                                .map(index -> new SletteTpForholdCommand(webClient, identer.get(index), miljoer, token.getTokenValue()).call())))
                .flatMap((Flux::from))
                .collectList()
                .subscribe(response -> log.info("Slettet mot PESYS (tp) i alle miljoer"));
    }

    @Timed(name = "providers", tags = {"operation", "pen_getTpForhold"})
    public JsonNode getTpForhold(String ident, String miljoe) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> new HentTpForholdCommand(webClient, token.getTokenValue(), ident, miljoe).call())
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpYtelse"})
    public Flux<PensjonforvalterResponse> lagreTpYtelse(PensjonTpYtelseRequest pensjonTpYtelseRequest) {

        log.info("Pensjon lagre TP-ytelse {}", pensjonTpYtelseRequest);
        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new LagreTpYtelseCommand(webClient, token.getTokenValue(), pensjonTpYtelseRequest).call());
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
