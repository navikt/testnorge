package no.nav.dolly.bestilling.aareg;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.aareg.domain.AaregOpprettRequest;
import no.nav.dolly.bestilling.aareg.domain.AaregResponse;
import no.nav.dolly.bestilling.aareg.domain.ArbeidsforholdResponse;
import no.nav.dolly.config.credentials.TestnorgeAaregProxyProperties;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;

@Slf4j
@Component
public class AaregConsumer {

    private static final String AAREGDATA_URL = "/api/v1/arbeidsforhold";
    private static final String IDENT_QUERY = "ident";
    private static final String MILJOE_QUERY = "miljoe";

    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;
    private final TokenService tokenService;

    public AaregConsumer(TestnorgeAaregProxyProperties serverProperties, TokenService tokenService, ObjectMapper objectMapper) {
        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        this.webClient = WebClient
                .builder()
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .baseUrl(serverProperties.getUrl())
                .build();
    }


    @Timed(name = "providers", tags = { "operation", "aareg_opprettArbeidforhold" })
    public AaregResponse opprettArbeidsforhold(AaregOpprettRequest request) {

        ResponseEntity<AaregResponse> response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(AAREGDATA_URL)
                        .build())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .bodyValue(request)
                .retrieve().toEntity(AaregResponse.class)
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException("Klarte ikke å opprette arbeidsforhold i testnorge-aareg");
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = { "operation", "aareg_getArbeidforhold" })
    public List<ArbeidsforholdResponse> hentArbeidsforhold(String ident, String miljoe) {

        try {
            ResponseEntity<List<ArbeidsforholdResponse>> response = webClient.get().uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                            .queryParam(IDENT_QUERY, ident)
                            .queryParam(MILJOE_QUERY, miljoe)
                            .build())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .retrieve().toEntityList(ArbeidsforholdResponse.class)
                    .block();

            if (nonNull(response) && !response.hasBody()) {
                throw new DollyFunctionalException(String.format("Klarte ikke å hente arbeidsforhold for %s i %s fra testnorge-aareg", ident, miljoe));
            }

            return response.hasBody() ? response.getBody() : emptyList();
        } catch (WebClientResponseException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                return emptyList();
            } else {
                throw e;
            }
        }
    }

    @Timed(name = "providers", tags = { "operation", "aareg_deleteArbeidsforhold" })
    public AaregResponse slettArbeidsforholdFraAlleMiljoer(String ident) {

        ResponseEntity<AaregResponse> response = webClient.delete().uri(uriBuilder -> uriBuilder.path(AAREGDATA_URL)
                        .queryParam(IDENT_QUERY, ident)
                        .build())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .retrieve().toEntity(AaregResponse.class)
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å slette arbeidsforhold for %s fra testnorge-aareg", ident));
        }

        return response.getBody();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}