package no.nav.registre.frikort.consumer.rs;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
public class HodejegerenHistorikkConsumer {

    private static final String MININORGE_KONTONUMMER = "20000723267";
    private static final int PAGE_SIZE = 100;

    private final RestTemplate restTemplate;
    private final UriTemplate hodejegerenHentGiroUrl;

    public HodejegerenHistorikkConsumer(
            @Value("${testnorge.hodejegeren.url}") String hodejegerenServerUrl,
            RestTemplate restTemplate
    ) {
        this.hodejegerenHentGiroUrl = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/soek/giro.nummer={gironummer}?kilder=skd&pageNumber={pageNumber}&pageSize={pageSize}");
        this.restTemplate = restTemplate;
    }

    public List<String> hentIdenterMedKontonummer(int antallIdenter) {
        var identerMedKontonummer = new HashSet<String>();
        int antallSider = antallIdenter / PAGE_SIZE;
        if (antallIdenter % PAGE_SIZE != 0) {
            antallSider++;
        }

        for (int i = 0; i < antallSider; i++) {
            var getRequest = RequestEntity.get(hodejegerenHentGiroUrl.expand(MININORGE_KONTONUMMER, i, PAGE_SIZE)).build();
            try {
                var response = restTemplate.exchange(getRequest, new ParameterizedTypeReference<List<JsonNode>>() {
                });
                if (response.getBody() != null) {
                    response.getBody().stream().map(identInfo -> identInfo.findValue("id").asText()).forEach(identerMedKontonummer::add);
                }
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke hente identer med kontonummer", e);
            }
        }
        if (identerMedKontonummer.size() < antallIdenter) {
            antallIdenter = identerMedKontonummer.size();
        }
        return new ArrayList<>(identerMedKontonummer).subList(0, antallIdenter);
    }
}
