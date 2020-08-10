package no.nav.dolly.bestilling.skjermingsregister;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeys.SYNTH_ENV;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkjermingsRegisterConsumer {

    private static final String SKJERMINGSREGISTER_URL = "/api/v1/skjermingdata";
    private static final String SKJERMINGOPPHOER_URL = SKJERMINGSREGISTER_URL + "/opphor/";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-opprett" })
    public ResponseEntity<List<SkjermingsDataResponse>> postSkjerming(List<SkjermingsDataRequest> skjermingsDataRequest) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGSREGISTER_URL))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(skjermingsDataRequest),
                new ParameterizedTypeReference<List<SkjermingsDataResponse>>() {
                });
    }

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-hent" })
    public ResponseEntity<List<SkjermingsDataResponse>> getSkjerming(String ident) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.get(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGSREGISTER_URL + "/" + ident))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER).build(),
                new ParameterizedTypeReference<List<SkjermingsDataResponse>>() {
                });
    }

    @Timed(name = "providers", tags = { "operation", "skjerminsdata-opphoer" })
    public ResponseEntity<String> putSkjerming(String ident) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.put(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGOPPHOER_URL + ident))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(),
                String.class);
    }

    @Timed(name = "providers", tags = { "operation", "skjerminsdata-slett" })
    public ResponseEntity<String> deleteSkjerming(String fnr) {

        String callid = getNavCallId();
        logInfoSkjermingsMelding(callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.delete(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGSREGISTER_URL + "/" + fnr))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(),
                String.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }

    private void logInfoSkjermingsMelding(String callId, String consumer) {

        log.info("Skjermingsmelding sendt, callid: {}, consumerId: {}", callId, consumer);
    }
}
