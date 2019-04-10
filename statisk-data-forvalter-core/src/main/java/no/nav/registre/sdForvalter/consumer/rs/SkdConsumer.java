package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.SkdRequest;
import no.nav.registre.sdForvalter.consumer.rs.response.SkdResponse;
import no.nav.registre.sdForvalter.database.model.TpsModel;

@Slf4j
@Component
public class SkdConsumer {

    private final RestTemplate restTemplate;
    private final String skdUrl;

    public SkdConsumer(RestTemplate restTemplate, @Value("${testnorge-skd.rest.api.url}") String skdUrl) {
        this.restTemplate = restTemplate;
        this.skdUrl = skdUrl + "/v1/syntetisering";
    }

    /**
     * @param data      Et set med personer som skal legges til i avspillergruppen
     * @param playgroup AvspillergruppeId som meldingene skal legges til på
     */
    public void createTpsMessagesInGroup(Set<TpsModel> data, Long playgroup) {
        //TODO: Figure out how to create new TPS messages in the group
        Set<SkdRequest> requests = data.parallelStream().map(t -> SkdRequest.builder()
                .dateOfBirth(t.getFnr().substring(0, 7))
                .fnr(t.getFnr().substring(7))
                .address(t.getAddress())
                .city(t.getCity())
                .firstName(t.getFirstName())
                .lastName(t.getLastName())
                .postnr(t.getPostNr())
                .build()).collect(Collectors.toSet());

        UriTemplate uriTemplate = new UriTemplate(skdUrl + "/startAvspilling/{playgroup}");
        RequestEntity<Set<SkdRequest>> requestEntity = new RequestEntity<>(requests, HttpMethod.POST, uriTemplate.expand(playgroup));
        ResponseEntity<List> responseEntity = restTemplate.exchange(requestEntity, List.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.warn("Kunne ikke opprette meldinger på gruppe {} i tpsf", playgroup);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @param playgroup   AvspillergruppeId som skal spilles av når denne funksjonen er invokert
     * @param environment Miljøet som gruppen skal spilles av til
     */
    public void send(Long playgroup, String environment) {
        UriTemplate uriTemplate = new UriTemplate(skdUrl + "/startAvspilling/{playgroup}?miljoe={environment}");
        RequestEntity<String> requestEntity = new RequestEntity<>(HttpMethod.POST, uriTemplate.expand(playgroup, environment));
        ResponseEntity<SkdResponse> responseEntity = restTemplate.exchange(requestEntity, SkdResponse.class);
        SkdResponse body = responseEntity.getBody();
        if (responseEntity.getStatusCode() != HttpStatus.OK && body != null) {
            if (body.getAntallFeilet() != 0) {
                log.warn("Fikk ikke opprettet alle identene i TPS, burde bli manuelt sjekket for overlapp. Kan også være mulig at man prøver å initialisere et miljø som er allerede initialisert");
                body.getFailedStatus().forEach(s -> log.warn("Status på feilende melding: {}", s));
            }
        }
    }
}
