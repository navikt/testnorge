package no.nav.registre.aareg.fasit;

import static java.lang.String.format;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.aareg.exception.FasitException;

@Component
@RequiredArgsConstructor
public class FasitApiConsumer {

    private static final String FASIT_RESOURCE = "/api/v2/resources?alias=%s&type=%s&usage=false&page=0&pr_page=100";

    private final RestTemplate restTemplate;

    @Value("${fasit.url}")
    private String fasitServerUrl;

    public FasitResourceWithUnmappedProperties[] fetchResources(String alias, String type) {
        var url = fasitServerUrl + format(FASIT_RESOURCE, alias, type);

        try {
            return restTemplate.getForEntity(url, FasitResourceWithUnmappedProperties[].class).getBody();
        } catch (HttpClientErrorException e) {
            throw new FasitException(e.getResponseBodyAsString(), e);
        }
    }
}
