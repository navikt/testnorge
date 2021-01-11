package no.nav.dolly.bestilling.organisasjonforvalter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.domain.AccessToken;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrganisasjonConsumer {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/v1/organisasjon";
    private static final String ORGANISASJON_BESTILLING_URL = ORGANISASJON_FORVALTER_URL + "/bestilling";
    private static final String ORGANISASJON_DEPLOYMENT_URL = ORGANISASJON_FORVALTER_URL + "/deployment";
    private static final String BEARER = "Bearer ";

    private final ProvidersProps providersProps;
    private final TokenService tokenService;
    private final WebClient webClient = WebClient.builder().build();

    @Value("${ORGANISASJON_FORVALTER_CLIENT_ID}")
    private String organisasjonerClientId;

    @Timed(name = "providers", tags = { "operation", "organisasjon-hent" })
    public ResponseEntity<BestillingRequest> hentOrganisasjon(List<Long> orgnumre) {

        String callId = getNavCallId();
        AccessToken accessToken = getAccessToken(callId, "Organisasjon hent request sendt, callId: {}, consumerId: {}");

        return webClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(providersProps.getOrganisasjonForvalter().getUrl() + ORGANISASJON_FORVALTER_URL)
                                .queryParam("orgnumre", orgnumre)
                                .build())
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(BestillingRequest.class)
                .block();
    }


    @Timed(name = "providers", tags = { "operation", "organisasjon-opprett" })
    public ResponseEntity<BestillingResponse> postOrganisasjon(BestillingRequest bestillingRequest) {

        String callId = getNavCallId();
        AccessToken accessToken = getAccessToken(callId, "Organisasjon oppretting sendt, callId: {}, consumerId: {}");

        return webClient
                .post()
                .uri(URI.create(providersProps.getOrganisasjonForvalter().getUrl() + ORGANISASJON_BESTILLING_URL))
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(bestillingRequest)
                .retrieve()
                .toEntity(BestillingResponse.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "organisasjon-deploy" })
    public ResponseEntity<DeployResponse> deployOrganisasjon(DeployRequest request) {

        String callId = getNavCallId();
        AccessToken accessToken = getAccessToken(callId, "Organisasjon deploy sendt, callId: {}, consumerId: {}");

        return webClient
                .post()
                .uri(URI.create(providersProps.getOrganisasjonForvalter().getUrl() + ORGANISASJON_DEPLOYMENT_URL))
                .header(AUTHORIZATION, BEARER + accessToken.getTokenValue())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .bodyValue(request)
                .retrieve()
                .toEntity(DeployResponse.class)
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }

    private AccessToken getAccessToken(String callId, String s) {
        log.info(s, callId, CONSUMER);

        return tokenService.getAccessToken(
                new AccessScopes("api://" + organisasjonerClientId + "/.default")
        );
    }
}
