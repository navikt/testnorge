package no.nav.dolly.bestilling.organisasjonforvalter;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.UUID;

import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.config.credentials.OrganisasjonForvalterProperties;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;

@Slf4j
@Service
public class OrganisasjonConsumer {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/v1/organisasjon";
    private static final String ORGANISASJON_BESTILLING_URL = ORGANISASJON_FORVALTER_URL + "/bestilling";
    private static final String ORGANISASJON_DEPLOYMENT_URL = ORGANISASJON_FORVALTER_URL + "/deployment";
    private static final String BEARER = "Bearer ";

    private final TokenService tokenService;
    private final WebClient webClient;
    private final OrganisasjonForvalterProperties serviceProperties;

    public OrganisasjonConsumer(TokenService tokenService, OrganisasjonForvalterProperties serviceProperties) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serviceProperties.getUrl())
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "organisasjon-hent"})
    public OrganisasjonDetaljer hentOrganisasjon(List<String> orgnumre) {
        var navCallId = getNavCallId();
        log.info("Organisasjon hent request sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        return tokenService.generateToken(serviceProperties).flatMap(accessToken -> webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(ORGANISASJON_FORVALTER_URL)
                                .queryParam("orgnumre", orgnumre)
                                .build())
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, navCallId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(OrganisasjonDetaljer.class)
        ).block();
    }


    @Timed(name = "providers", tags = {"operation", "organisasjon-opprett"})
    public ResponseEntity<BestillingResponse> postOrganisasjon(BestillingRequest bestillingRequest) {
        var navCallId = getNavCallId();
        log.info("Organisasjon oppretting sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);
        return tokenService.generateToken(serviceProperties).flatMap(accessToken -> webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_BESTILLING_URL).build())
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, navCallId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(bestillingRequest)
                .retrieve()
                .toEntity(BestillingResponse.class)
        ).block();
    }

    @Timed(name = "providers", tags = {"operation", "organisasjon-deploy"})
    public ResponseEntity<DeployResponse> deployOrganisasjon(DeployRequest request) {
        var navCallId = getNavCallId();
        log.info("Organisasjon deploy sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        AccessToken accessToken = tokenService.generateToken(serviceProperties).block();
        return sendDeployOrganisasjonRequest(request, navCallId, accessToken);
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
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }

}
