package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Set;

import no.nav.registre.sdForvalter.consumer.rs.response.SkdResponse;
import no.nav.registre.sdForvalter.database.model.TpsModel;

@Slf4j
@Component
public class SkdConsumer implements ConsumerInitializer {

    private final RestTemplate restTemplate;
    private final String skdUrl;

    public SkdConsumer(RestTemplate restTemplate, @Value("${testnorge-skd.rest.api.url}") String skdUrl) {
        this.restTemplate = restTemplate;
        this.skdUrl = skdUrl + "/v1/syntetisering";
    }

    /**
     * @param data      Et set med felter som skal legges til i gruppen
     * @param playgroup AvspillergruppeId som meldingene skal legges til på
     */
    public void createTpsMessagesInGroup(Set<TpsModel> data, Long playgroup) {
        //TODO: Figure out how to create new TPS messages in the group
    }

    /**
     * @param data        Et sett med avspillergrupper som skal spilles av når denne funksjonen er invokert
     * @param environment Miljøet som gruppen skal spilles av til
     */
    @Override
    public void send(Set<Object> data, String environment) {
        UriTemplate uriTemplate = new UriTemplate(skdUrl + "/startAvspilling/{playgroupId}");
        for (Object playgroup : data) {
            RequestEntity<String> requestEntity = new RequestEntity<>(environment, HttpMethod.POST, uriTemplate.expand(playgroup));
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
}
