package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Set;

@Slf4j
@Component
public class HodejegerenConsumer {

    private final RestTemplate restTemplate;
    private final String hodejegerenUrl;

    public HodejegerenConsumer(RestTemplate restTemplate, @Value("${testnorge-hodejegeren.rest.api.url}") String hodejegerenUrl) {
        this.restTemplate = restTemplate;
        this.hodejegerenUrl = hodejegerenUrl + "/api/v1";
    }

    /**
     * @param playgroupId AvspillergruppeId som man ønsker å hente fnr fra
     * @return Et set med fnr som eksisterer i gruppen
     */
    public Set<String> getPlaygroupFnrs(Long playgroupId) {
        UriTemplate uriTemplate = new UriTemplate(hodejegerenUrl + "/alle-identer/{avspillergruppeId}");
        return restTemplate.getForObject(uriTemplate.expand(playgroupId), Set.class);
    }
}
