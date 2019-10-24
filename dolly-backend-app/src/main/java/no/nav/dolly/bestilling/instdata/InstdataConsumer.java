package no.nav.dolly.bestilling.instdata;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.properties.ProvidersProps;

@Service
@RequiredArgsConstructor
public class InstdataConsumer {

    private static final String INSTDATA_URL = "/api/v1/ident";
    private static final String NAV_CALL_ID = "navCallId";
    private static final String NAV_CONSUMER_ID = "navConsumerId";
    private static final String CONSUMER = "Dolly";
    private static final String DELETE_FMT_BLD = "%s" + INSTDATA_URL + "/batch?identer=%s&miljoe=%s";
    private static final String POST_FMT_BLD = "%s" + INSTDATA_URL + "/batch?miljoe=%s";
    private static final String INSTMILJO_URL = "%s/api/v1/miljoer";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;

    public ResponseEntity getMiljoer() {
        return restTemplate.exchange(
                RequestEntity.get(URI.create(format(INSTMILJO_URL, providersProps.getInstdata().getUrl())))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .build(), String[].class);
    }

    public ResponseEntity deleteInstdata(String ident, String environment) {
        return restTemplate.exchange(
                RequestEntity.delete(URI.create(format(DELETE_FMT_BLD, providersProps.getInstdata().getUrl(), ident, environment)))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .build(), InstdataResponse[].class);
    }

    public ResponseEntity postInstdata(List<Instdata> instdata, String environment) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(format(POST_FMT_BLD, providersProps.getInstdata().getUrl(), environment)))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .body(instdata), InstdataResponse[].class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
