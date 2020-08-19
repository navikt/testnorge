package no.nav.registre.inntekt.consumer.rs.altinninntekt;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.rs.RsInntektsmelding;

@Slf4j
@Component
public class AltinnInntektConsumer {

    private final RestTemplate restTemplate;
    private final UriTemplate urlMapper201812;

    public AltinnInntektConsumer(
            @Value("${altinnInntekt.rest.api.url}") String altinnInntektUrl,
            RestTemplate restTemplate
    ) {
        urlMapper201812 = new UriTemplate(altinnInntektUrl + "/v2/inntektsmelding/2018/12/11");

        this.restTemplate = restTemplate;
    }

    public String getInntektsmeldingXml201812(
            RsInntektsmelding inntektsmelding
    ) {
        var postRequest = RequestEntity.post(urlMapper201812.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(inntektsmelding);
        try {
            return restTemplate.exchange(postRequest, String.class).getBody();
        } catch (RestClientResponseException e) {
            log.error("Uventet feil ved mapping til AltinnInntekt.", e);
            throw e;
        }
    }
}
