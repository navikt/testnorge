package no.nav.dolly.bestilling.aareg;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.ConsumerStatus;
import no.nav.dolly.bestilling.aareg.command.ArbeidsforholdGetCommand;
import no.nav.dolly.bestilling.aareg.command.ArbeidsforholdPostCommand;
import no.nav.dolly.bestilling.aareg.command.ArbeidsforholdPutCommand;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.reactivecore.logging.WebClientLogger;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class AaregConsumer extends ConsumerStatus {

    private final WebClient webClient;
    private final ServerProperties serverProperties;
    private final TokenExchange tokenService;

    public AaregConsumer(
            Consumers consumers,
            TokenExchange tokenService,
            ObjectMapper objectMapper,
            WebClient webClient,
            WebClientLogger webClientLogger
    ) {
        serverProperties = consumers.getTestnavAaregProxy();
        this.tokenService = tokenService;
        var webClientBuilder = webClient
                .mutate();
        webClientLogger.customize(webClientBuilder);
        this.webClient = webClientBuilder
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "aareg_opprettArbeidforhold"})
    public Flux<ArbeidsforholdRespons> opprettArbeidsforhold(Arbeidsforhold request, String miljoe) {

        log.info("AAREG: Oppretting av arbeidsforhold, miljø {} request {}", miljoe, request);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new ArbeidsforholdPostCommand(webClient, miljoe, request, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "aareg_endreArbeidforhold"})
    public Flux<ArbeidsforholdRespons> endreArbeidsforhold(Arbeidsforhold request, String miljoe) {

        log.info("AAREG: Oppdatering av arbeidsforhold, miljø {} request {}", miljoe, request);
        return tokenService.exchange(serverProperties)
                .flatMapMany(token ->
                        new ArbeidsforholdPutCommand(webClient, miljoe, request, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "aareg_getArbeidforhold"})
    public Mono<ArbeidsforholdRespons> hentArbeidsforhold(String ident, String miljoe) {

        log.info("AAREG: Henting av arbeidsforhold, miljø {} ident {}", miljoe, ident);
        return tokenService.exchange(serverProperties)
                .flatMap(token ->
                        new ArbeidsforholdGetCommand(webClient, miljoe, ident, token.getTokenValue()).call());
    }

    @Override
    public String serviceUrl() {
        return serverProperties.getUrl();
    }

    @Override
    public String consumerName() {
        return "testnav-aareg-proxy";
    }

}
