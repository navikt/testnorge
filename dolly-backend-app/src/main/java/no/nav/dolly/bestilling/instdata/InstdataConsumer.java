package no.nav.dolly.bestilling.instdata;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.instdata.domain.InstdataResponse;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Slf4j
@Service
@RequiredArgsConstructor
public class InstdataConsumer {

    private static final String INSTDATA_URL = "/api/v1/ident";
    private static final String DELETE_FMT_BLD = "%s" + INSTDATA_URL + "/batch?identer=%s&miljoe=%s";
    private static final String POST_FMT_BLD = "%s" + INSTDATA_URL + "/batch?miljoe=%s";
    private static final String INSTMILJO_URL = "%s/api/v1/miljoer";
    private static final String GET_FMT_BLD = "%s" + INSTDATA_URL + "?identer=%s&miljoe=%s";

    private static final String[] DEFAULT_ENV = { "q2" };

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    @Timed(name = "providers", tags = { "operation", "inst_getMiljoer" })
    public String[] getMiljoer() {

        try {
            return restTemplate.exchange(
                    RequestEntity.get(URI.create(format(INSTMILJO_URL, providersProps.getInstdata().getUrl())))
                            .header(HEADER_NAV_CALL_ID, getNavCallId())
                            .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                            .build(), String[].class).getBody();

        } catch (RuntimeException e) {
            log.error("Kunne ikke lese fra endepunkt for aa hente miljoer: {} ", e.getMessage(), e);
            return DEFAULT_ENV;
        }
    }

    @Timed(name = "providers", tags = { "operation", "inst_getInstdata" })
    public ResponseEntity getInstdata(String ident, String environment) {
        return restTemplate.exchange(
                RequestEntity.get(URI.create(format(GET_FMT_BLD, providersProps.getInstdata().getUrl(), ident, environment)))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(), Instdata[].class);
    }

    @Timed(name = "providers", tags = { "operation", "inst_deleteInstdata" })
    public ResponseEntity deleteInstdata(String ident, String environment) {
        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format(DELETE_FMT_BLD, providersProps.getInstdata().getUrl(), ident, environment)))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .build(), InstdataResponse[].class);
    }

    @Timed(name = "providers", tags = { "operation", "inst_postInstdata" })
    public ResponseEntity postInstdata(List<Instdata> instdata, String environment) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(format(POST_FMT_BLD, providersProps.getInstdata().getUrl(), environment)))
                        .header(HEADER_NAV_CALL_ID, getNavCallId())
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .body(instdata), InstdataResponse[].class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
