package no.nav.dolly.bestilling.organisasjonforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.organisasjonforvalter.command.GetOrganisasjonCommand;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.BestillingResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployRequest;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.DeployResponse;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDeployStatus;
import no.nav.dolly.bestilling.organisasjonforvalter.domain.OrganisasjonDetaljer;
import no.nav.dolly.config.Consumers;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class OrganisasjonConsumer {

    private static final String ORGANISASJON_FORVALTER_URL = "/api/v2/organisasjoner";
    private static final String ORGANISASJON_DEPLOYMENT_URL = ORGANISASJON_FORVALTER_URL + "/ordre";
    private static final String ORGANISASJON_STATUS_URL = ORGANISASJON_FORVALTER_URL + "/ordrestatus";
    private static final String BEARER = "Bearer ";

    private final TokenExchange tokenService;
    private final WebClient webClient;
    private final Consumers.OrganisasjonForvalter serviceProperties;

    public OrganisasjonConsumer(
            TokenExchange tokenService,
            Consumers.OrganisasjonForvalter serviceProperties,
            ObjectMapper objectMapper,
            WebClient.Builder webClientBuilder
    ) {
        this.tokenService = tokenService;
        this.serviceProperties = serviceProperties;
        this.webClient = webClientBuilder
                .baseUrl(serviceProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = {"operation", "organisasjon-hent"})
    public Flux<OrganisasjonDetaljer> hentOrganisasjon(List<String> orgnumre) {

        return tokenService.exchange(serviceProperties)
                .flatMapMany(token -> new GetOrganisasjonCommand(webClient, orgnumre, token.getTokenValue()).call());
    }

    @Timed(name = "providers", tags = {"operation", "organisasjon-hent"})
    public OrganisasjonDeployStatus hentOrganisasjonStatus(List<String> orgnumre) {
        var navCallId = getNavCallId();
        log.info("Organisasjon hent request sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient
                        .get()
                        .uri(uriBuilder ->
                                uriBuilder.path(ORGANISASJON_STATUS_URL)
                                        .queryParam("orgnumre", orgnumre)
                                        .build())
                        .header(AUTHORIZATION, BEARER + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .header(HEADER_NAV_CALL_ID, navCallId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .retrieve()
                        .bodyToMono(OrganisasjonDeployStatus.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)))
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "organisasjon-opprett"})
    public ResponseEntity<BestillingResponse> postOrganisasjon(BestillingRequest bestillingRequest) {

        var navCallId = getNavCallId();
        log.info("Organisasjon oppretting sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);
        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient
                        .post()
                        .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_FORVALTER_URL).build())
                        .header(AUTHORIZATION, BEARER + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .header(HEADER_NAV_CALL_ID, navCallId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .bodyValue(bestillingRequest)
                        .retrieve()
                        .toEntity(BestillingResponse.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)))
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "organisasjon-deploy"})
    public ResponseEntity<DeployResponse> deployOrganisasjon(DeployRequest request) {
        var navCallId = getNavCallId();
        log.info("Organisasjon deploy sendt, callId: {}, consumerId: {}", navCallId, CONSUMER);

        return sendDeployOrganisasjonRequest(request, navCallId);
    }

    private ResponseEntity<DeployResponse> sendDeployOrganisasjonRequest(DeployRequest deployRequest, String callId) {

        return tokenService.exchange(serviceProperties)
                .flatMap(token -> webClient
                        .post()
                        .uri(uriBuilder -> uriBuilder.path(ORGANISASJON_DEPLOYMENT_URL).build())
                        .header(AUTHORIZATION, BEARER + token.getTokenValue())
                        .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                        .header(HEADER_NAV_CALL_ID, callId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .bodyValue(deployRequest)
                        .retrieve()
                        .toEntity(DeployResponse.class)
                        .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                                .filter(WebClientFilter::is5xxException)))
                .block();
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}
