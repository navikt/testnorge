package no.nav.registre.inntekt.consumer.rs;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inntekt.domain.dokmot.JoarkResponse;
import no.nav.registre.inntekt.provider.rs.requests.DokmotRequest;
import no.nav.registre.inntekt.security.sts.StsOidcService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Slf4j
@Component
public class DokmotConsumer {

    private final RestTemplate restTemplate;
    private final StsOidcService oidcService;

    private final UriTemplate url;


    public DokmotConsumer(
            @Value("${dokmot.joark.rest.api.url}") String joarkUrl,
            RestTemplate restTemplate,
            StsOidcService oidcService
    ) {
        url = new UriTemplate(joarkUrl + "/rest/journalpostapi/v1/journalpost");
        this.restTemplate = restTemplate;
        this.oidcService = oidcService;
    }

    public JoarkResponse opprettJournalpost(DokmotRequest opprettJournalpostRequest, String miljoe) {
        RequestEntity request = RequestEntity.post(url.expand(miljoe))
                .header(AUTHORIZATION, oidcService.getIdToken(miljoe))
                .body(opprettJournalpostRequest);
        try {
            return restTemplate.exchange(request, JoarkResponse.class).getBody();
        } catch (Exception e) {
            log.error("Uventet feil ved lagring av journalpost", e);
        }
        return null;
    }
}
