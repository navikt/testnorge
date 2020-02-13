package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.aareg.AaregRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidsforhold;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidsgiver;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidstaker;
import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.database.model.AaregModel;

@Slf4j
@Component
public class AaregConsumer {

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

        List<AaregRequest> requestBody = data.parallelStream().map(d -> new AaregRequest(
                new Arbeidsforhold(
                        new Arbeidsgiver(
                                Long.toString(d.getOrgId())
                        ),
                        new Arbeidstaker(
                                d.getFnr()
                        )
                ),
                Collections.singletonList(environment)
        )).collect(Collectors.toList());

        for (AaregRequest request : requestBody) {
            log.info("Sender ident {} til miljøer {}.", request.getArbeidsforhold().getArbeidstaker().getIdent(), request.getEnvironments().toString());
        }

        UriTemplate uriTemplate = new UriTemplate(aaregUrl + "/sendTilAareg?fyllUtArbeidsforhold=true");
        RequestEntity postRequest = RequestEntity.post(uriTemplate.expand()).body(requestBody);
        return restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<AaregResponse>>() {
        }).getBody();
    }
}
