package no.nav.registre.inntekt.consumer.rs;

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

    private static final ParameterizedTypeReference<List<Inntektsmelding>> RESPONSE_OPPRETT =
            new ParameterizedTypeReference<List<Inntektsmelding>>() {};

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate urlOpprett201809;
    private UriTemplate urlOpprett201812;
    private UriTemplate urlGetXML201809;
    private UriTemplate urlGetXML201812;


    public AltinnInntektConsumer(@Value("${altinnInntekt.rest.api.url}") String altinnInntektUrl) {
        urlOpprett201809 = new UriTemplate(altinnInntektUrl + "/2018/09?eier={eier}");
        urlOpprett201812 = new UriTemplate(altinnInntektUrl + "/2018/12?eier={eier}");
        urlGetXML201809 = new UriTemplate(altinnInntektUrl + "/2018/09/xml/{id}");
        urlGetXML201812 = new UriTemplate(altinnInntektUrl + "/2018/12/xml/{id}");
    }

    public List<String> getInntektsmeldingXml201809(List<AltinnInntektRequest> inntektsmeldinger, String eier) {
        List<String> opprettedeMeldinger = new ArrayList<>(inntektsmeldinger.size());

        RequestEntity postRequest = RequestEntity.post(urlOpprett201809.expand(eier)).body(inntektsmeldinger);
        try {
            List<Inntektsmelding> lagredeMeldinger = restTemplate.exchange(postRequest, RESPONSE_OPPRETT).getBody();
            if (lagredeMeldinger != null) {
                opprettedeMeldinger.addAll(lagredeMeldinger.stream().map(melding -> {
                    RequestEntity getXmlMelding = RequestEntity.get(urlGetXML201809.expand(melding.getId())).build();
                    return restTemplate.exchange(getXmlMelding, String.class).getBody();
                }).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("Uventet feil ved lagring til AltinnInntekt.", e);
        }
        return opprettedeMeldinger;
    }

    public List<String> getInntektsmeldingXml201812(List<AltinnInntektRequest> inntektsmeldinger, String eier) {
        List<String> opprettedeMeldinger = new ArrayList<>(inntektsmeldinger.size());

        RequestEntity postRequest = RequestEntity.post(urlOpprett201812.expand(eier)).body(inntektsmeldinger);
        try {
            List<Inntektsmelding> lagredeMeldinger = restTemplate.exchange(postRequest, RESPONSE_OPPRETT).getBody();
            if (lagredeMeldinger != null) {
                opprettedeMeldinger.addAll(lagredeMeldinger.stream().map(melding -> {
                    RequestEntity getXmlMelding = RequestEntity.get(urlGetXML201812.expand(melding.getId())).build();
                    return restTemplate.exchange(getXmlMelding, String.class).getBody();
                }).collect(Collectors.toList()));
            }
        } catch (Exception e) {
            log.error("Uventet feil ved lagring til AltinnInntekt.", e);
        }
        return opprettedeMeldinger;
    }

}
