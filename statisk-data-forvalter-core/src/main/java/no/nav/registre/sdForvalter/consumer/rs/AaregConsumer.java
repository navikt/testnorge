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
import java.util.Map;
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
    private static final ParameterizedTypeReference<List<Object>> RESPONSE_TYPE_LIST = new ParameterizedTypeReference<List<Object>>() {
    };
    private final String aaregStubUrl;

    public AaregConsumer(RestTemplate restTemplate, @Value("${testnorge.aareg.rest.api.url}") String aaregUrl, @Value("${testnorge.aaregstub.rest.api.url}") String aaregStubUrl) {
        this.restTemplate = restTemplate;
        this.aaregUrl = aaregUrl + "/v1/syntetisering";
        this.aaregStubUrl = aaregStubUrl + "/v1";
    }

    public Set<String> finnPersonerUtenArbeidsforhold(Set<String> fnrs, String environment) {
        UriTemplate uriTemplate = new UriTemplate(aaregStubUrl + "/hentArbeidsforholdFraAareg?ident={fnr}&miljoe={environment}");
        log.info(fnrs.toString());
        return fnrs.stream().map(f -> {
            RequestEntity request = new RequestEntity<>(HttpMethod.GET, uriTemplate.expand(f, environment));
            ResponseEntity<List<Object>> response = restTemplate.exchange(request, RESPONSE_TYPE_LIST);
            if (response.getBody() != null) {
                if (!response.getStatusCode().is2xxSuccessful()) {
                    log.warn("Klarte ikke opprette arbeidsforhold for {}", response.getBody());
                }
                if (response.getBody().isEmpty()) {
                    return f;
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public Map<String, String> send(Set<AaregModel> data, String environment) {

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

        UriTemplate uriTemplate = new UriTemplate(aaregUrl + "/sendTilAareg?fyllUtArbeidsforhold=true");
        RequestEntity<List<AaregRequest>> request = new RequestEntity<>(requestBody, HttpMethod.POST, uriTemplate.expand());
        ResponseEntity<AaregResponse> response = restTemplate.exchange(request, AaregResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                log.warn("Klarte ikke opprette arbeidsforhold for {}", response.getBody().getIdenterSomIkkeKunneLagresIAareg());
            }
        }
        return Objects.requireNonNull(response.getBody()).getIdenterSomIkkeKunneLagresIAareg();
    }
}
