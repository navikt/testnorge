package no.nav.dolly.consumer.fastedatasett;

import static java.lang.String.*;

import java.net.URI;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;

@Component
@RequiredArgsConstructor
public class FasteDatasettConsumer {

    private static final String REQUEST_URL = "/api/v1/faste-data";
    private static final String GET_GRUPPE_URL = "/{datasettType}?gruppe=";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    @Timed(name = "providers", tags = {"operation", "hentFasteDatasett"})
    public ResponseEntity hentDatasett(DatasettType datasettType) {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s%s", providersProps.getFasteDatasett().getUrl(), REQUEST_URL, datasettType.getUrl())))
                .build(), JsonNode.class);
    }

    @Timed(name = "providers", tags = {"operation", "hentOrgnummer"})
    public ResponseEntity hentOrgnummer() {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(format("%s%s/ereg?gruppe=DOLLY", providersProps.getFasteDatasett().getUrl(), REQUEST_URL)))
                .build(), JsonNode.class);
    }

    @Timed(name = "providers", tags = {"operation", "hentFasteDatasett"})
    public ResponseEntity hentDatasettGruppe(DatasettType datasettType, String gruppe) {

        return restTemplate.exchange(RequestEntity.get(
                URI.create(format(providersProps.getFasteDatasett().getUrl()+ REQUEST_URL + datasettType.getUrl()+"?gruppe="+gruppe)))
                .build(), JsonNode.class);
    }
}
