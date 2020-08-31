package no.nav.registre.testnorge.person.consumer.command;

import static org.reflections.Reflections.log;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.pdl.OpprettPersonDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import no.nav.registre.testnorge.person.exception.PdlCreatePersonException;

@RequiredArgsConstructor
public class PostOpprettPersonCommand implements Callable<OpprettPersonDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final String token;

    @Override
    public OpprettPersonDTO call() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(PdlHeaders.NAV_PERSONIDENT, ident);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<OpprettPersonDTO> exchange = restTemplate.exchange(
                url + "/api/v1/bestilling/opprettperson",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                OpprettPersonDTO.class
        );

        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.info("Noe gikk galt under opprett person {}", exchange.getStatusCode());
            throw new PdlCreatePersonException("Noe gikk galt under opprett person");
        }
        return exchange.getBody();
    }
}
