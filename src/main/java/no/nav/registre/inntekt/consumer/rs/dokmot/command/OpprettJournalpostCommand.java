package no.nav.registre.inntekt.consumer.rs.dokmot.command;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotRequest;
import no.nav.registre.inntekt.consumer.rs.dokmot.dto.DokmotResponse;


@Slf4j
@AllArgsConstructor
public class OpprettJournalpostCommand implements Callable<DokmotResponse> {
    private final RestTemplate restTemplate;
    private final String token;
    private final URI url;
    private final DokmotRequest request;
    private final String navCallId;

    @Override
    public DokmotResponse call() {
        try {
            DokmotResponse response = restTemplate.exchange(
                    RequestEntity.post(url)
                            .header("Nav-Call-Id", navCallId)
                            .header(AUTHORIZATION, "Bearer " + token)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(this.request),
                    DokmotResponse.class
            ).getBody();

            if (response == null) {
                throw new RuntimeException("Response fra dokmot er null");
            }

            log.info("En jorunalpost sendt til dokmotak med journalpost id {} og {} dokument(er)",
                    response.getJournalpostId(),
                    response.getDokumenter() != null ? response.getDokumenter().size() : 0
            );

            return response;
        } catch (Exception e) {
            log.error("Uventet feil ved lagring av journalpost", e);
            throw e;
        }
    }
}
