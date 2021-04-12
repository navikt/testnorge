package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.config.credentials.OrganisasjonForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class OrganisasjonConsumer {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/v1/organisasjon";
    private static final String ORGANISASJON_BESTILLING_URL = ORGANISASJON_FORVALTER_URL + "/bestilling";
    private static final String ORGANISASJON_DEPLOYMENT_URL = ORGANISASJON_FORVALTER_URL + "/deployment";
    private static final String BEARER = "Bearer ";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final OrganisasjonForvalterProperties organisasjonForvalterProperties;

    public OrganisasjonConsumer(TokenService tokenService, OrganisasjonForvalterProperties organisasjonForvalterProperties) {
        this.tokenService = tokenService;
        this.organisasjonForvalterProperties = organisasjonForvalterProperties;
        this.webClient = WebClient.builder()
                .baseUrl(organisasjonForvalterProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-opprett" })
    public ResponseEntity<BestillingResponse> postOrganisasjon(BestillingRequest bestillingRequest) {

        AccessToken accessToken = getAccessToken("Organisasjon oppretting sendt, callId: {}, consumerId: {}");

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_BESTILLING_URL).build())
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(bestillingRequest)
                .retrieve()
                .toEntity(BestillingResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-deploy" })
    public ResponseEntity<DeployResponse> deployOrganisasjon(DeployRequest request) {

        AccessToken accessToken = getAccessToken("Organisasjon deploy sendt, callId: {}, consumerId: {}");
        return sendDeployOrganisasjonRequest(request, getNavCallId(), accessToken);
    }

    private ResponseEntity<DeployResponse> sendDeployOrganisasjonRequest(DeployRequest deployRequest, String callId, AccessToken accessToken) {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_DEPLOYMENT_URL).build())
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(deployRequest)
                .retrieve()
                .toEntity(DeployResponse.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }

    private AccessToken getAccessToken(String s) {
        log.info(s, getNavCallId(), CONSUMER);

        return tokenService.generateToken(organisasjonForvalterProperties);
    }
}
