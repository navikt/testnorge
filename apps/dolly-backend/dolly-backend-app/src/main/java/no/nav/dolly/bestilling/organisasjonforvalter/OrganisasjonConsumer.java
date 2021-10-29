package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployStatus;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.config.credentials.OrganisasjonForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.service.TokenService;
import no.nav.dolly.util.CheckAliveUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class OrganisasjonConsumer {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/v2/organisasjoner";
    private static final String ORGANISASJON_DEPLOYMENT_URL = ORGANISASJON_FORVALTER_URL + "/ordre";
    private static final String ORGANISASJON_STATUS_URL = ORGANISASJON_FORVALTER_URL + "/ordrestatus";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final OrganisasjonForvalterProperties serviceProperties;

    public OrganisasjonConsumer(TokenService tokenService, OrganisasjonForvalterProperties serviceProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-hent" })
    public OrganisasjonDetaljer hentOrganisasjon(List<String> orgnumre) {
        var navCallId = getNavCallId();
        log.info("Organisasjon hent request sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(ORGANISASJON_FORVALTER_URL)
                                .queryParam("orgnumre", orgnumre)
                                .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, navCallId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(OrganisasjonDetaljer.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-hent" })
    public OrganisasjonDeployStatus hentOrganisasjonStatus(List<String> orgnumre) {
        var navCallId = getNavCallId();
        log.info("Organisasjon hent request sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(ORGANISASJON_STATUS_URL)
                                .queryParam("orgnumre", orgnumre)
                                .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, navCallId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(OrganisasjonDeployStatus.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-opprett" })
    public ResponseEntity<BestillingResponse> postOrganisasjon(BestillingRequest bestillingRequest) {
        var navCallId = getNavCallId();
        log.info("Organisasjon oppretting sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_FORVALTER_URL).build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, navCallId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(bestillingRequest)
                .retrieve()
                .toEntity(BestillingResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-deploy" })
    public ResponseEntity<DeployResponse> deployOrganisasjon(DeployRequest request) {
        var navCallId = getNavCallId();
        log.info("Organisasjon deploy sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        return sendDeployOrganisasjonRequest(request, navCallId);
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-alive" })
    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private ResponseEntity<DeployResponse> sendDeployOrganisasjonRequest(DeployRequest deployRequest, String callId) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_DEPLOYMENT_URL).build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(deployRequest)
                .retrieve()
                .toEntity(DeployResponse.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
