package no.nav.dolly.bestilling.udistub;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.config.credentials.UdistubServerProperties;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.JacksonExchangeStrategyUtil;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@Slf4j
@Service
public class UdiStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final WebClient webClient;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public UdiStubConsumer(
            ErrorStatusDecoder errorStatusDecoder,
            TokenExchange accessTokenService,
            UdistubServerProperties serverProperties,
            ObjectMapper objectMapper
    ) {
        this.tokenService = accessTokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(JacksonExchangeStrategyUtil.getJacksonStrategy(objectMapper))
                .build();
        this.errorStatusDecoder = errorStatusDecoder;
    }

    @Timed(name = "providers", tags = { "operation", "udi_getPerson" })
    public UdiPersonResponse getUdiPerson(String ident) {

        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON)
                            .pathSegment(ident).build())
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .retrieve()
                    .bodyToMono(UdiPersonResponse.class)
                    .block();

        } catch (RuntimeException e) {
            throw new DollyFunctionalException(format("Feilet å hente UDI-person: %s",
                    errorStatusDecoder.decodeRuntimeException(e)), e);
        }
    }

    @Timed(name = "providers", tags = { "operation", "udi_createPerson" })
    public UdiPersonResponse createUdiPerson(UdiPerson udiPerson) {

        try {
            return webClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                    .accept(MediaType.APPLICATION_JSON)
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                    .retrieve()
                    .bodyToMono(UdiPersonResponse.class)
                    .block();
        } catch (RuntimeException e) {

            throw new DollyFunctionalException(format("Feilet å opprette UDI-person: %s",
                    errorStatusDecoder.decodeRuntimeException(e)), e);
        }
    }


    @Timed(name = "providers", tags = { "operation", "udi_updatePerson" })
    public UdiPersonResponse updateUdiPerson(UdiPerson udiPerson) {

        try {
            return webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                    .retrieve()
                    .bodyToMono(UdiPersonResponse.class)
                    .block();
        } catch (RuntimeException e) {

            throw new DollyFunctionalException(format("Feilet å oppdatere UDI-person: %s",
                    errorStatusDecoder.decodeRuntimeException(e)), e);
        }
    }

    @Timed(name = "providers", tags = { "operation", "udi_deletePerson" })
    public void deleteUdiPerson(String ident) {

        try {
            webClient
                    .put()
                    .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(NAV_PERSON_IDENT, ident)
                    .header(HttpHeaders.AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                    .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                    .retrieve().toBodilessEntity()
                    .block();

        } catch (RuntimeException e) {
            errorStatusDecoder.decodeRuntimeException(e);
        }
    }

    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID());
    }
}