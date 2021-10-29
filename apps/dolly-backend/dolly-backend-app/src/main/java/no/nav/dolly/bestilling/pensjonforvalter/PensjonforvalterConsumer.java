package no.nav.dolly.bestilling.pensjonforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.config.credentials.PensjonforvalterProxyProperties;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.config.NaisServerProperties;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class PensjonforvalterConsumer {

    private static final String API_VERSJON = "/api/v1";
    private static final String PENSJON_OPPRETT_PERSON_URL = API_VERSJON + "/person";
    private static final String MILJOER_HENT_TILGJENGELIGE_URL = API_VERSJON + "/miljo";
    private static final String PENSJON_INNTEKT_URL = API_VERSJON + "/inntekt";
    private static final String FNR_QUERY = "fnr";
    private static final String MILJO_QUERY = "miljo";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final NaisServerProperties serviceProperties;

    public PensjonforvalterConsumer(TokenService tokenService, PensjonforvalterProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "pen_getMiljoer" })
    public Set<String> getMiljoer() {

        try {
            ResponseEntity<String[]> responseEntity = webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder
                            .path(MILJOER_HENT_TILGJENGELIGE_URL)
                            .build())
                    .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
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

    @Timed(name = "providers", tags = { "operation", "pen_opprettPerson" })
    public PensjonforvalterResponse opprettPerson(OpprettPersonRequest opprettPersonRequest) {

        ResponseEntity<PensjonforvalterResponse> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_OPPRETT_PERSON_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(opprettPersonRequest)
                .retrieve().toEntity(PensjonforvalterResponse.class)
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException("Klarte ikke å opprette person i pensjon-testdata-facade");
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreInntekt" })
    public PensjonforvalterResponse lagreInntekt(LagreInntektRequest lagreInntektRequest) {


        ResponseEntity<PensjonforvalterResponse> response = webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_INNTEKT_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(lagreInntektRequest)
                .retrieve().toEntity(PensjonforvalterResponse.class)
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å lagre inntekt for %s i pensjon-testdata-facade", lagreInntektRequest.getFnr()));
        }

        return response.getBody();
    }

    @Timed(name = "providers", tags = { "operation", "pen_getInntekter" })
    public JsonNode getInntekter(String ident, String miljoe) {


        ResponseEntity<JsonNode> response = webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_INNTEKT_URL)
                        .queryParam(FNR_QUERY, ident)
                        .queryParam(MILJO_QUERY, miljoe)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve().toEntity(JsonNode.class)
                .block();

        if (nonNull(response) && !response.hasBody()) {
            throw new DollyFunctionalException(String.format("Klarte ikke å hente inntekt for %s i %s fra pensjon-testdata-facade", ident, miljoe));
        }

        return response.getBody();
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

}
