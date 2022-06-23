package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Collections.emptySet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class PensjonforvalterConsumer {

    private static final String API_VERSJON = "/api/v1";
    private static final String PENSJON_OPPRETT_PERSON_URL = API_VERSJON + "/person";
    private static final String MILJOER_HENT_TILGJENGELIGE_URL = API_VERSJON + "/miljo";
    private static final String PENSJON_INNTEKT_URL = API_VERSJON + "/inntekt";
    private static final String PENSJON_TP_FORHOLD_URL = API_VERSJON + "/tp/forhold";
    private static final String PENSJON_TP_PERSON_FORHOLD_URL = API_VERSJON + "/tp/person/forhold";
    private static final String PENSJON_TP_YTELSE_URL = API_VERSJON + "/tp/ytelse";
    private static final String FNR_QUERY = "fnr";
    private static final String MILJO_QUERY = "miljo";

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
            ResponseEntity<String[]> responseEntity = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MILJOER_HENT_TILGJENGELIGE_URL)
                            .build())
                    .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .header(HEADER_NAV_CALL_ID, generateCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .retrieve().toEntity(String[].class)
                    .block();

            return responseEntity.hasBody() ? new HashSet<>(Set.of(responseEntity.getBody())) : emptySet();

        } catch (RuntimeException e) {

            log.error("Feilet å lese tilgjengelige miljøer fra pensjon. {}", e.getMessage(), e);
            return emptySet();
        }
    }

    @Timed(name = "providers", tags = {"operation", "pen_opprettPerson"})
    public PensjonforvalterResponse opprettPerson(OpprettPersonRequest opprettPersonRequest) {

        ResponseEntity<PensjonforvalterResponse> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_OPPRETT_PERSON_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(opprettPersonRequest)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException("Klarte ikke å opprette person i pensjon-testdata-facade");
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreInntekt"})
    public PensjonforvalterResponse lagreInntekt(LagreInntektRequest lagreInntektRequest) {


        ResponseEntity<PensjonforvalterResponse> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_INNTEKT_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreInntektRequest)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å lagre inntekt for %s i pensjon-testdata-facade", lagreInntektRequest.getFnr()));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_getInntekter"})
    public JsonNode getInntekter(String ident, String miljoe) {


        ResponseEntity<JsonNode> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_INNTEKT_URL)
                        .queryParam(FNR_QUERY, ident)
                        .queryParam(MILJO_QUERY, miljoe)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å hente inntekt for %s i %s fra pensjon-testdata-facade", ident, miljoe));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpForhold"})
    public PensjonforvalterResponse lagreTpForhold(LagreTpForholdRequest lagreTpForholdRequest) {

        ResponseEntity<PensjonforvalterResponse> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_FORHOLD_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpForholdRequest)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
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
    public void sletteTpForhold(String pid) {

        ResponseEntity<PensjonforvalterResponse> response = webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_PERSON_FORHOLD_URL)
                        .queryParam("miljoer", "q1,q2,q4")
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("pid", pid)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (isNull(response) || isNull(response.getBody()) || !response.hasBody()) {
            log.info("Sletting mot TP forhold utført");
        } else {
            var status = response.getBody().getStatus().stream().map(s -> {
                var httpStatus = s.getResponse().getHttpStatus();
                return s.getMiljo() + ":" +
                        (httpStatus != null ? httpStatus.getReasonPhrase() : "");
            }).collect(Collectors.joining(", "));

            log.info("Sletting mot TP forhold utført: {}", status);
        }
    }

    @Timed(name = "providers", tags = {"operation", "pen_getTpForhold"})
    public JsonNode getTpForhold(String ident, String miljoe) {

        ResponseEntity<JsonNode> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_FORHOLD_URL)
                        .queryParam(FNR_QUERY, ident)
                        .queryParam(MILJO_QUERY, miljoe)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

        if (isNull(response) || !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å hente TP forhold for %s i %s fra TP (pensjon)", ident, miljoe));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = {"operation", "pen_lagreTpYtelse"})
    public PensjonforvalterResponse lagreTpYtelse(LagreTpYtelseRequest lagreTpYtelseRequest) {

        ResponseEntity<PensjonforvalterResponse> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_YTELSE_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreTpYtelseRequest)
                .retrieve()
                .toEntity(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
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
