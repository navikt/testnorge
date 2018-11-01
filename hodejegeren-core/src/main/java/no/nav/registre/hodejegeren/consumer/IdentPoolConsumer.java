package no.nav.registre.hodejegeren.consumer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;

@Component
public class IdentPoolConsumer {
    
    @Value("${ident-pool.rest-api.url}")
    private String serverUrl;
    private RestTemplate restTemplate;
    
    public IdentPoolConsumer() {
        this.restTemplate = new RestTemplate();
        ObjectMapper OBJECT_MAPPER = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.restTemplate.setMessageConverters(Arrays.asList(new MappingJackson2HttpMessageConverter(OBJECT_MAPPER)));
    }
    
    public List<String> hentNyeIdenter(HentIdenterRequest hentIdenterRequest) {
        return restTemplate.postForObject(serverUrl + "/v1/identifikator", hentIdenterRequest, ArrayList.class);
    }
}
