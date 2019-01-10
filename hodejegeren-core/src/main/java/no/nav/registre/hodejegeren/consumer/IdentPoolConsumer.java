package no.nav.registre.hodejegeren.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;

@Component
public class IdentPoolConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };
    private URI url;
    private RestTemplate restTemplate;

    public IdentPoolConsumer(@Value("${ident-pool.rest-api.url}") String serverUrl) throws URISyntaxException {
        this.restTemplate = new RestTemplate();
        ObjectMapper objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(objectMapper)));

        this.url = new URI(serverUrl + "/v1/identifikator");
    }

    public List<String> hentNyeIdenter(HentIdenterRequest hentIdenterRequest) {
        RequestEntity postRequest = RequestEntity.post(url).body(hentIdenterRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
