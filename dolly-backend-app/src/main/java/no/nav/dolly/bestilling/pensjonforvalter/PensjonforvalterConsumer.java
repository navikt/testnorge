package no.nav.dolly.bestilling.pensjonforvalter;

import static java.lang.String.format;
import static java.util.Collections.emptySet;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.OpprettPersonRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PensjonforvalterConsumer {

    private static final String API_VERSJON = "/api/v1";
    private static final String PENSJON_OPPRETT_PERSON_URL = API_VERSJON + "/person";
    private static final String MILJOER_HENT_TILGJENGELIGE_URL = API_VERSJON + "/miljo";
    private static final String PENSJON_INNTEKT_URL = API_VERSJON + "/inntekt";
    private static final String PREPROD_ENV = "q";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "pen_getMiljoer" })
    public Set<String> getMiljoer() {

        try {
            ResponseEntity responseEntity = restTemplate.exchange(
                    RequestEntity.get(URI.create(providersProps.getPensjonforvalter().getUrl() + MILJOER_HENT_TILGJENGELIGE_URL))
                            .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                            .header(HEADER_NAV_CALL_ID, getCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .build(),
                    String[].class);
            return responseEntity.hasBody() ? new HashSet(Set.of((String[]) responseEntity.getBody())) : emptySet();

        } catch (RuntimeException e) {

            log.error("Feilet å lese tilgjengelige miljøer fra pensjon. {}", e.getMessage(), e);
            return emptySet();
        }
    }

    @Timed(name = "providers", tags = { "operation", "pen_opprettPerson" })
    public PensjonforvalterResponse opprettPerson(OpprettPersonRequest opprettPersonRequest) {

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getPensjonforvalter().getUrl() + PENSJON_OPPRETT_PERSON_URL))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                        .header(HEADER_NAV_CALL_ID, getCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(opprettPersonRequest),
                PensjonforvalterResponse.class).getBody();
    }

    @Timed(name = "providers", tags = { "operation", "pen_lagreInntekt" })
    public PensjonforvalterResponse lagreInntekt(LagreInntektRequest lagreInntektRequest) {

        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getPensjonforvalter().getUrl() + PENSJON_INNTEKT_URL))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                        .header(HEADER_NAV_CALL_ID, getCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(lagreInntektRequest),
                PensjonforvalterResponse.class).getBody();
    }

    @Timed(name = "providers", tags = { "operation", "pen_getInntekter" })
    public ResponseEntity getInntekter(String ident, String miljoe) {

        return restTemplate.exchange(
                RequestEntity.get(URI.create(
                        format("%s%s?fnr=%s&miljo=%s",
                                providersProps.getPensjonforvalter().getUrl(),
                                PENSJON_INNTEKT_URL, ident, miljoe)))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                        .header(HEADER_NAV_CALL_ID, getCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(), JsonNode.class);
    }

    private static String getCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
