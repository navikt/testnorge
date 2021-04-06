package no.nav.registre.skd.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import no.nav.registre.skd.consumer.requests.HentIdenterRequest;
import no.nav.registre.skd.consumer.response.Navn;

@Slf4j
@Component
public class IdentPoolConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<List<Navn>> RESPONSE_TYPE_NAVN = new ParameterizedTypeReference<>() {
    };

    private final String baseUrl;
    private final RestTemplate restTemplate;

    public IdentPoolConsumer(@Value("${ident-pool.rest-api.url}") String serverUrl) {
        this.restTemplate = new RestTemplate();
        var objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        this.restTemplate.setMessageConverters(Collections.singletonList(new MappingJackson2HttpMessageConverter(objectMapper)));
        this.baseUrl = serverUrl;

    }

    @Timed(value = "skd.resource.latency", extraTags = { "operation", "identpool" })
    public List<String> hentNyeIdenter(HentIdenterRequest hentIdenterRequest) {
        RequestEntity<HentIdenterRequest> postRequest = null;
        try {
            postRequest = RequestEntity.post(new URI(this.baseUrl + "/v1/identifikator?finnNaermesteLedigeDato=false")).body(hentIdenterRequest);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return restTemplate.exchange(Objects.requireNonNull(postRequest), RESPONSE_TYPE).getBody();
    }

    public Navn hentNavn() {
        RequestEntity<?> request = null;
        try {
            request = RequestEntity.get(new URI(this.baseUrl + "/v1/fiktive-navn/tilfeldig?antall=1")).build();
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        var navn = restTemplate.exchange(Objects.requireNonNull(request), RESPONSE_TYPE_NAVN).getBody();
        if (navn == null) {
            log.error("Kunne ikke hente navn");
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Kunne ikke hente navn");
        }
        if (navn.size() != 1) {
            log.error("Fikk feil antall navn - størrelse: {}", navn.size());
        }
        return navn.get(0);
    }

    public List<String> frigjoerLedigeIdenter(List<String> identer) {
        RequestEntity<List<String>> postRequest = RequestEntity.post(new UriTemplate(this.baseUrl + "/v1/identifikator/frigjoerLedige").expand())
                .body(identer);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<String>>() {
        }).getBody();
    }
}
