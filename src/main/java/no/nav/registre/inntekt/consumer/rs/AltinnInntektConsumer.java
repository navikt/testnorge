package no.nav.registre.inntekt.consumer.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.domain.altinn.rs.AltinnInntektRequest;
import no.nav.registre.inntekt.domain.altinn.db.Inntektsmelding;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Component
public class AltinnInntektConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate urlMapper201809;
    private UriTemplate urlMapper201812;


    public AltinnInntektConsumer(@Value("${altinnInntekt.rest.api.url}") String altinnInntektUrl) {
        urlMapper201809 = new UriTemplate(altinnInntektUrl + "/v1/inntektsmelding/map/2018/09");
        urlMapper201812 = new UriTemplate(altinnInntektUrl + "/v1/inntektsmelding/map/2018/12");
    }

    public String getInntektsmeldingXml201809(AltinnInntektRequest inntektsmelding) {
        RequestEntity postRequest = RequestEntity.post(urlMapper201809.expand()).header("Content-Type", "application/json").body(inntektsmelding);
        try {
            return restTemplate.exchange(postRequest, String.class).getBody();
        } catch (Exception e) {
            log.info("Uventet feil ved mapping til AltinnInntekt.", e);
        }
        return "";
    }

    public String getInntektsmeldingXml201812(AltinnInntektRequest inntektsmelding) {
        RequestEntity postRequest = RequestEntity.post(urlMapper201812.expand()).header("Content-Type", "application/json").body(inntektsmelding);
        try {
            return restTemplate.exchange(postRequest, String.class).getBody();
        } catch (Exception e) {
            log.info("Uventet feil ved mapping til AltinnInntekt.", e);
        }
        return "";
    }

}
