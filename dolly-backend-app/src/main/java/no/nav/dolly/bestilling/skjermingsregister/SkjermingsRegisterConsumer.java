package no.nav.dolly.bestilling.skjermingsregister;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.CommonKeys.SYNTH_ENV;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.skjermingsregister.domain.SkjermingsDataRequest;
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

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }

    @Timed(name = "providers", tags = { "operation", "skjermingsdata-opprett" })
    public ResponseEntity<String> postSkjerming(SkjermingsDataRequest skjermingsDataRequest) {

        String callid = getNavCallId();
        log.info("Skjermings melding sendt, callid: {}, consumerId: {}", callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGSREGISTER_URL))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(skjermingsDataRequest),
                String.class);
    }

    @Timed(name = "providers", tags = { "operation", "skjerminsdata-opphoer" })
    public ResponseEntity<String> putSkjerming(String fnr) {

        String callid = getNavCallId();
        log.info("Skjermings melding sendt, callid: {}, consumerId: {}", callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.put(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGOPPHOER_URL + fnr))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(),
                String.class);
    }

    @Timed(name = "providers", tags = { "operation", "skjerminsdata-slett" })
    public ResponseEntity<String> deleteSkjerming(String fnr) {

        String callid = getNavCallId();
        log.info("Skjermings melding sendt, callid: {}, consumerId: {}", callid, CONSUMER);

        return restTemplate.exchange(
                RequestEntity.delete(URI.create(providersProps.getSkjermingsRegister().getUrl() + SKJERMINGSREGISTER_URL + "/" + fnr))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(SYNTH_ENV))
                        .header(HEADER_NAV_CALL_ID, callid)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(),
                String.class);
    }
}
