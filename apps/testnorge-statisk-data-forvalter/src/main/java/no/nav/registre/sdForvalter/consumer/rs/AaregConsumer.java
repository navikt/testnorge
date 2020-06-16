package no.nav.registre.sdForvalter.consumer.rs;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.AaregRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidsforhold;
import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.domain.AaregListe;

@Slf4j
@Component
public class AaregConsumer {

    private static final ParameterizedTypeReference<List<AaregResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<AaregResponse>>() {
    };

    private final RestTemplate restTemplate;
    private final UriTemplate sendArbeidsforholdTilAaregUrl;
    private final UriTemplate getArbeidsforholdFraAaregUrl;

    public AaregConsumer(
            RestTemplate restTemplate,
            @Value("${testnorge.aareg.rest.api.url}") String aaregServerUrl
    ) {
        this.restTemplate = restTemplate;
        this.sendArbeidsforholdTilAaregUrl = new UriTemplate(aaregServerUrl + "/v1/syntetisering/sendTilAareg?fyllUtArbeidsforhold=true");
        this.getArbeidsforholdFraAaregUrl = new UriTemplate(aaregServerUrl + "/v1/ident/{ident}?miljoe={miljoe}");
    }

    public void sendArbeidsforhold(AaregListe liste, String environment) {
        List<AaregRequest> requestList = liste.getListe()
                .stream()
                // TODO filter arbeidsforhold som allerede eksisterer.
                // .filter(item -> getArbeidsforhold(item.getFnr(), environment).isEmpty())
                .map(item -> new AaregRequest(new Arbeidsforhold(item), environment))
                .collect(Collectors.toList());

        try {
            restTemplate.exchange(
                    RequestEntity.post(sendArbeidsforholdTilAaregUrl.expand()).body(requestList),
                    RESPONSE_TYPE
            ).getBody();
        } catch (Exception e) {
            log.error("Feil ved innsending av arbeidsforhold", e);
            throw e;
        }
    }
}
