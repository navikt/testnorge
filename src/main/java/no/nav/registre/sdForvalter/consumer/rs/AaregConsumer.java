package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.aareg.AaregRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidsforhold;
import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.database.model.AaregModel;

@Slf4j
@Component
public class AaregConsumer {

    public static final ParameterizedTypeReference<List<AaregResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<AaregResponse>>() {
    };
    private final RestTemplate restTemplate;
    private final String aaregUrl;

    public AaregConsumer(RestTemplate restTemplate, @Value("${testnorge.aareg.rest.api.url}") String aaregUrl
    ) {
        this.restTemplate = restTemplate;
        this.aaregUrl = aaregUrl + "/v1/syntetisering";
    }

    public List<AaregResponse> send(
            Set<AaregModel> data,
            String environment
    ) {

        List<AaregRequest> requestList = data
                .stream()
                .map(d -> new AaregRequest(new Arbeidsforhold(d), environment))
                .collect(Collectors.toList());

        for (AaregRequest request : requestList) {
            log.info(
                    "Sender ident {} til miljøer {}.",
                    request.getArbeidsforhold().getArbeidstaker().getIdent(),
                    String.join(", ", request.getEnvironments())
            );
        }
        UriTemplate uriTemplate = new UriTemplate(aaregUrl + "/sendTilAareg?fyllUtArbeidsforhold=true");
        return restTemplate.exchange(
                RequestEntity.post(uriTemplate.expand()).body(requestList),
                RESPONSE_TYPE
        ).getBody();
    }
}
