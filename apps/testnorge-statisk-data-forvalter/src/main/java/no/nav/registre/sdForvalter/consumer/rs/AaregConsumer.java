package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.aareg.AaregRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidsforhold;
import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.domain.AaregListe;

@Slf4j
@Component
public class AaregConsumer {

    private static final ParameterizedTypeReference<List<AaregResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<AaregResponse>>() {
    };
    private static final String CALL_ID = "Orkestratoren";
    private static final String CONSUMER_ID = "Orkestratoren";

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

    private List getArbeidsforhold(String ident, String miljoe) {
        RequestEntity<?> getRequest = RequestEntity.get(getArbeidsforholdFraAaregUrl.expand(ident, miljoe))
                .header("Nav-Call-Id", CALL_ID)
                .header("Nav-Consumer-Id", CONSUMER_ID)
                .build();
        try {
            List list = restTemplate.exchange(getRequest, List.class).getBody();
            return list != null ? list : new ArrayList();
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke hente arbeidsforhold fra aareg i miljø {}", miljoe);
        }
        return new ArrayList();
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
