package no.nav.dolly.bestilling.aareg;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.command.AaregDeleteCommand;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.config.credentials.TestnorgeAaregProxyProperties;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Component
public class AaregConsumer {

    private static final String AAREGDATA_URL = "/api/v1/arbeidsforhold";
    private static final String IDENT_QUERY = "ident";
    private static final String MILJOE_QUERY = "miljoe";

    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final TokenExchange tokenService;

    public AaregConsumer(TestnorgeAaregProxyProperties serverProperties, TokenExchange tokenService, ObjectMapper objectMapper) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.webClient = WebClient
            .builder()
            .exchangeStrategies(getJacksonStrategy(objectMapper))
            .baseUrl(serverProperties.getUrl())
            .build();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

    @Timed(name = "providers", tags = {"operation", "aareg_opprettArbeidforhold"})
    public AaregResponse opprettArbeidsforhold(AaregOpprettRequest request) {

        ResponseEntity<AaregResponse> response = webClient
            .post()
            .uri(uriBuilder -> uriBuilder
                .path(AAREGDATA_URL)
                .build())
            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
            .header(HEADER_NAV_CALL_ID, getNavCallId())
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
            .header(UserConstant.USER_HEADER_JWT, getUserJwt())
            .bodyValue(request)
            .retrieve()
            .toEntity(AaregResponse.class)
            .retryWhen(Retry
                .backoff(3, Duration.ofSeconds(5))
                .filter(WebClientFilter::is5xxException))
            .block();
        return Optional
            .ofNullable(response)
            .filter(HttpEntity::hasBody)
            .orElseThrow(() -> new DollyFunctionalException("Klarte ikke å opprette arbeidsforhold i testnorge-aareg"))
            .getBody();

    }

    @Timed(name = "providers", tags = {"operation", "aareg_getArbeidforhold"})
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

        try {
            ResponseEntity<List<ArbeidsforholdResponse>> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                    .path(AAREGDATA_URL)
                    .queryParam(IDENT_QUERY, ident)
                    .queryParam(MILJOE_QUERY, miljoe)
                    .build())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toEntityList(ArbeidsforholdResponse.class)
                .retryWhen(Retry
                    .backoff(3, Duration.ofSeconds(5))
                    .filter(WebClientFilter::is5xxException))
                .block();
            return Optional
                .ofNullable(response)
                .filter(HttpEntity::hasBody)
                .orElseThrow(() -> new DollyFunctionalException(format("Klarte ikke å hente arbeidsforhold for %s i %s fra testnorge-aareg", ident, miljoe)))
                .getBody();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return emptyList();
            }
            throw e;
        }

    }

    @Timed(name = "providers", tags = {"operation", "aareg_deleteArbeidsforhold"})
    public Mono<List<AaregResponse>> slettArbeidsforholdFraAlleMiljoer(List<String> identer) {

        return tokenService.exchange(serviceProperties)
            .flatMapMany(token -> Flux.range(0, identer.size())
                .map(index -> new AaregDeleteCommand(webClient, identer.get(index), token.getTokenValue()).call()
                    .filter(response -> !response.getStatusPerMiljoe().isEmpty()))
                .flatMap(Flux::from))
            .collectList();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

}