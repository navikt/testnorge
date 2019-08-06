package no.nav.dolly.fasit;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.exceptions.FasitException;
import no.nav.dolly.properties.ProvidersProps;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public class FasitApiConsumer {

    private static final String FASIT_RESOURCE = "/api/v2/resources?alias=%s&type=%s&usage=false&page=0&pr_page=100";

    private final ProvidersProps providersProps;
    private final RestTemplate restTemplate;

    public FasitResourceWithUnmappedProperties[] fetchResources(String alias, String type) {
        String url = providersProps.getFasit().getUrl() + format(FASIT_RESOURCE, alias, type);

        try {
            ResponseEntity<FasitResourceWithUnmappedProperties[]> properties = restTemplate.getForEntity(url, FasitResourceWithUnmappedProperties[].class);
            return properties.getBody();
        } catch (HttpClientErrorException e) {
            throw new FasitException(e.getStatusCode(), e.getResponseBodyAsString());
        }
    }
}
