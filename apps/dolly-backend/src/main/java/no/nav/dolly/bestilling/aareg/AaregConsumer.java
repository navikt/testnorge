package no.nav.dolly.bestilling.aareg;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.command.ArbeidsforholdGetCommand;
import no.nav.dolly.bestilling.aareg.command.ArbeidsforholdPostCommand;
import no.nav.dolly.bestilling.aareg.command.ArbeidsforholdPutCommand;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdRespons;
import no.nav.dolly.config.credentials.TestnavAaregisterProxyProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.dto.aareg.v1.Arbeidsforhold;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class AaregConsumer {

    private final WebClient webClient;
    private final ServerProperties serviceProperties;
    private final TokenExchange tokenService;

    public AaregConsumer(TestnavAaregisterProxyProperties serverProperties, TokenExchange tokenService, ObjectMapper objectMapper) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }

    public Mono<AccessToken> getAccessToken() {

        return tokenService.exchange(serviceProperties);
    }

    @Timed(name = "providers", tags = {"operation", "aareg_opprettArbeidforhold"})
    public Flux<ArbeidsforholdRespons> opprettArbeidsforhold(Arbeidsforhold request, String miljoe, AccessToken token) {

        log.info("AAREG: Oppretting av arbeidsforhold, miljø {} request {}", miljoe, request);
        return new ArbeidsforholdPostCommand(webClient, miljoe, request, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "aareg_endreArbeidforhold"})
    public Flux<ArbeidsforholdRespons> endreArbeidsforhold(Arbeidsforhold request, String miljoe, AccessToken token) {

        log.info("AAREG: Oppdatering av arbeidsforhold, miljø {} request {}", miljoe, request);
        return new ArbeidsforholdPutCommand(webClient, miljoe, request, token.getTokenValue()).call();
    }

    @Timed(name = "providers", tags = {"operation", "aareg_getArbeidforhold"})
    public Mono<ArbeidsforholdRespons> hentArbeidsforhold(String ident, String miljoe, AccessToken token) {

        log.info("AAREG: Henting av arbeidsforhold, miljø {} ident {}", miljoe, ident);
        return new ArbeidsforholdGetCommand(webClient, miljoe, ident, token.getTokenValue()).call();
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

//        var pensjonStatus = CheckAliveUtil.checkConsumerStatus(
//                "https://pensjon-testdata-facade.dev.intern.nav.no/isAlive",
//                "https://pensjon-testdata-facade.dev.intern.nav.no/isReady",
//                WebClient.builder().build());
//        pensjonStatus.put("team", "Pensjon");

        return Map.of(
                "AAREG", statusMap
        );
    }
}
