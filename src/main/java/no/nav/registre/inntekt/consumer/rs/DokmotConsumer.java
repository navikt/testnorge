package no.nav.registre.inntekt.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.domain.dokmot.JoarkResponse;
import no.nav.registre.inntekt.provider.rs.requests.DokmotRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Slf4j
@Component
public class DokmotConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;


    public DokmotConsumer(@Value("${dokmot.jorark.rest.api.url}") String joarkUrl) {
        url = new UriTemplate(joarkUrl + "/rest/journalpostapi/v1/journalpost");
    }

    public JoarkResponse opprettJournalpost(DokmotRequest opprettJournalpostRequest) {
        RequestEntity request = RequestEntity.post(url.expand()).body(opprettJournalpostRequest);
        try {
            return restTemplate.exchange(request, JoarkResponse.class).getBody();
        } catch (Exception e) {
            log.error("Uventet feil ved lagring av journalpost");
        }
        return null;
    }
}
