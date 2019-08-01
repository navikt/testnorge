package no.nav.registre.inst.fasit;

import static java.lang.String.format;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.inst.properties.ProvidersProps;

@Service
public class FasitApiConsumer {

    private static final String FASIT_RESOURCE = "/api/v2/resources?alias=%s&type=%s&usage=false&page=0&pr_page=100";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProvidersProps providersProps;

    public FasitResourceWithUnmappedProperties[] fetchResources(String alias, String type) {
        String url = providersProps.getFasit().getUrl() + format(FASIT_RESOURCE, alias, type);

        try {
            ResponseEntity<FasitResourceWithUnmappedProperties[]> properties = restTemplate.getForEntity(url, FasitResourceWithUnmappedProperties[].class);
            return properties.getBody();
        } catch (HttpClientErrorException e) {
            throw new FasitException(e.getStatusCode(), e.getResponseBodyAsString(), e);
        }
    }
}
