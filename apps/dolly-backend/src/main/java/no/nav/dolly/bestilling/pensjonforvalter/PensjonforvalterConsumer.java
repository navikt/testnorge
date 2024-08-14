package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.pensjonforvalter.command.AnnullerSamboerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.HentMiljoerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.HentSamboerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreAlderspensjonCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagrePensjonsavtaleCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagrePoppInntektCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreSamboerCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreTpYtelseCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.LagreUforetrygdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.OpprettPersonCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.SlettePensjonsavtaleCommand;
import no.nav.dolly.bestilling.pensjonforvalter.command.SletteTpForholdCommand;
import no.nav.dolly.bestilling.pensjonforvalter.domain.AlderspensjonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonPoppInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpForholdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonTpYtelseRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonUforetrygdRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonsavtaleRequest;
import no.nav.dolly.config.Consumers;
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
    private final ServerProperties serverProperties;

    public PensjonforvalterConsumer(
            TokenExchange tokenService,
            Consumers consumers,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        serverProperties = consumers.getTestnavPensjonTestdataFacadeProxy();
        this.webClient = webClientBuilder
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "pen_getMiljoer" })
    public Mono<Set<String>> getMiljoer() {

        return tokenService.exchange(serverProperties)
                .flatMap(token -> new HentMiljoerCommand(webClient, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "popp_lagreInntekt" })
    public Flux<PensjonforvalterResponse> lagreInntekter(PensjonPoppInntektRequest pensjonPoppInntektRequest) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new LagrePoppInntektCommand(webClient, token.getTokenValue(),
                        pensjonPoppInntektRequest).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_opprettPerson" })
    public Flux<PensjonforvalterResponse> opprettPerson(PensjonPersonRequest pensjonPersonRequest,
                                                        Set<String> miljoer) {

        pensjonPersonRequest.setMiljoer(miljoer);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new OpprettPersonCommand(webClient, pensjonPersonRequest, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Opprettet person for {}: {}", pensjonPersonRequest.getFnr(), response));
    }

    @Timed(name = "providers", tags = { "operation", "pen_hentSamboer" })
    public Flux<PensjonSamboerResponse> hentSamboer(String ident, String miljoe) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new HentSamboerCommand(webClient, ident, miljoe, token.getTokenValue()).call())
                .doOnNext(response -> log.info("Pensjon samboer for {} i {} hentet {}", ident, miljoe, response));
    }

    @Timed(name = "providers", tags = { "operation", "pen_opprettSamboer" })
    public Flux<PensjonforvalterResponse> lagreSamboer(PensjonSamboerRequest pensjonSamboerRequest,
                                                       String miljoe) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new LagreSamboerCommand(webClient, pensjonSamboerRequest, miljoe, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_opprettSamboer" })
    public Flux<PensjonforvalterResponse> annullerSamboer(String periodeId, String miljoe) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new AnnullerSamboerCommand(webClient, periodeId, miljoe, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreAlderspensjon" })
    public Flux<PensjonforvalterResponse> lagreAlderspensjon(AlderspensjonRequest request) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new LagreAlderspensjonCommand(webClient, token.getTokenValue(), request).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreUforetrygd" })
    public Flux<PensjonforvalterResponse> lagreUforetrygd(PensjonUforetrygdRequest request) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new LagreUforetrygdCommand(webClient, token.getTokenValue(), request).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreTpForhold" })
    public Flux<PensjonforvalterResponse> lagreTpForhold(PensjonTpForholdRequest pensjonTpForholdRequest) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new LagreTpForholdCommand(webClient, token.getTokenValue(), pensjonTpForholdRequest).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_sletteTpForhold" })
    public void sletteTpForhold(List<String> identer) {

        tokenService.exchange(serverProperties)
                .flatMapMany(token -> new HentMiljoerCommand(webClient, token.getTokenValue()).call()
                        .flatMapMany(miljoer -> Flux.range(0, identer.size())
                                .map(index -> new SletteTpForholdCommand(webClient, identer.get(index), miljoer, token.getTokenValue()).call())))
                .flatMap((Flux::from))
                .collectList()
                .subscribe(response -> log.info("Slettet mot PESYS (tp) i alle miljoer"));
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreTpYtelse" })
    public Flux<PensjonforvalterResponse> lagreTpYtelse(PensjonTpYtelseRequest pensjonTpYtelseRequest) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new LagreTpYtelseCommand(webClient, token.getTokenValue(), pensjonTpYtelseRequest).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagrePensjpnsavtale" })
    public Flux<PensjonforvalterResponse> lagrePensjonsavtale(PensjonsavtaleRequest pensjonsavtaleRequest) {

        return tokenService.exchange(serverProperties)
                .flatMapMany(token -> new LagrePensjonsavtaleCommand(webClient, pensjonsavtaleRequest, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = { "operation", "pen_slettePensjpnsavtale" })
    public void slettePensjonsavtale(List<String> identer) {

        var test = tokenService.exchange(serverProperties)
                .flatMapMany(token -> Flux.fromIterable(identer)
                        .flatMap(ident -> new SlettePensjonsavtaleCommand(webClient, ident, token.getTokenValue()).call()))
                .collectList()
                .subscribe(resultat -> log.info("Slettet pensjonsavtaler (PEN), alle miljøer"));
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-pensjon-testdata-facade-proxy";
    }

}
