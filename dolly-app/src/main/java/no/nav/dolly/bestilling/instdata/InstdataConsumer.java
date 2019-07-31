package no.nav.dolly.bestilling.instdata;

import static java.lang.String.format;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import no.nav.dolly.domain.resultset.inst.Instdata;
import no.nav.dolly.properties.ProvidersProps;

@Service
public class InstdataConsumer {

    private static final String INSTDATA_URL = "/api/v1/ident";
    private static final String NAV_CALL_ID = "navCallId";
    private static final String NAV_CONSUMER_ID = "navConsumerId";
    private static final String CONSUMER = "Dolly";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public ResponseEntity getInstdata(List<String> identer) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getInstdata().getUrl() + INSTDATA_URL))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .body(identer), JsonNode.class);
    }

    public ResponseEntity deleteInstdata(List<String> identer) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getInstdata().getUrl() + INSTDATA_URL))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .body(identer), JsonNode.class);
    }

    public ResponseEntity postInstdata(List<Instdata> instdata) {
        return restTemplate.exchange(
                RequestEntity.post(URI.create(providersProps.getInstdata().getUrl() + INSTDATA_URL))
                        .header(NAV_CALL_ID, getNavCallId())
                        .header(NAV_CONSUMER_ID, CONSUMER)
                        .body(instdata), JsonNode.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
