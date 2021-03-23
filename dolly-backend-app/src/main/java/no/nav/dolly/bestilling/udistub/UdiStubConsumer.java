package no.nav.dolly.bestilling.udistub;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.udistub.domain.UdiPerson;
import no.nav.dolly.bestilling.udistub.domain.UdiPersonResponse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.oauth2.domain.AccessScopes;
import no.nav.dolly.security.oauth2.service.TokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;

@Slf4j
@Service
public class UdiStubConsumer {

    private static final String CONSUMER = "Dolly";
    private static final String NAV_PERSON_IDENT = "Nav-Personident";
    private static final String UDISTUB_PERSON = "/api/v1/person";

    private final TokenService accessTokenService;
    private final WebClient webClient;
    private final ErrorStatusDecoder errorStatusDecoder;
    private final String clientId;

    public UdiStubConsumer(
            ErrorStatusDecoder errorStatusDecoder,
            TokenService accessTokenService,
            ProvidersProps providersProps,
            @Value("${UDI_STUB_CLIENT_ID}") String clientId
    ) {
        this.accessTokenService = accessTokenService;
        this.webClient = WebClient.builder()
                .baseUrl(providersProps.getUdiStub().getUrl()).build();
        this.errorStatusDecoder = errorStatusDecoder;
        this.clientId = clientId;
    }

    @Timed(name = "providers", tags = { "operation", "udi_getPerson" })
    public UdiPersonResponse getUdiPerson(String ident) {

        try {
            return webClient
                    .get()
                    .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON + "/" + ident).build())
                    .header(HEADER_NAV_CALL_ID, getNavCallId())
                    .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .retrieve()
                    .bodyToMono(UdiPersonResponse.class)
                    .block();

        } catch (RuntimeException e) {
            return null;
        }
    }

    @Timed(name = "providers", tags = { "operation", "udi_createPerson" })
    public UdiPersonResponse createUdiPerson(UdiPerson udiPerson) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .accept(MediaType.APPLICATION_JSON)
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                .retrieve()
                .bodyToMono(UdiPersonResponse.class)
                .block();
    }


    @Timed(name = "providers", tags = { "operation", "udi_updatePerson" })
    public UdiPersonResponse updateUdiPerson(UdiPerson udiPerson) {

        return webClient
                .put()
                .uri(uriBuilder -> uriBuilder.path(UDISTUB_PERSON).build())
                .header(HEADER_NAV_CALL_ID, getNavCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                .body(BodyInserters.fromPublisher(Mono.just(udiPerson), UdiPerson.class))
                .retrieve()
                .bodyToMono(UdiPersonResponse.class)
                .block();
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
                    .header(HttpHeaders.AUTHORIZATION, getBearerToken())
                    .exchange()
                    .block();

        } catch (RuntimeException e) {
            errorStatusDecoder.decodeRuntimeException(e);
        }
    }

    private String getBearerToken() {
        String tokenValue = accessTokenService.generateToken(
                new AccessScopes("api://" + clientId + "/.default")
        ).getTokenValue();
        return "Bearer " + tokenValue;
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}