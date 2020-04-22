package no.nav.registre.inntekt.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.inntekt.domain.altinn.rs.RsInntektsmelding;


@Slf4j
@Component
public class AltinnInntektConsumer {

    private RestTemplate restTemplate;

    private UriTemplate urlMapper201809;
    private UriTemplate urlMapper201812;


    public AltinnInntektConsumer(@Value("${altinnInntekt.rest.api.url}") String altinnInntektUrl, RestTemplate restTemplate) {
        urlMapper201809 = new UriTemplate(altinnInntektUrl + "/v1/inntektsmelding/map/2018/09");
        urlMapper201812 = new UriTemplate(altinnInntektUrl + "/v2/inntektsmelding/2018/12/11");

        this.restTemplate = restTemplate;
    }

    public String getInntektsmeldingXml201812(RsInntektsmelding inntektsmelding, Boolean continueOnError) {
        RequestEntity postRequest = RequestEntity.post(urlMapper201812.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(inntektsmelding);
        try {
            return restTemplate.exchange(postRequest, String.class).getBody();
        } catch (Exception e) {
            log.error("Uventet feil ved mapping til AltinnInntekt.", e);
            if(!continueOnError){
                throw e;
            }
        }
        return "";
    }
}
