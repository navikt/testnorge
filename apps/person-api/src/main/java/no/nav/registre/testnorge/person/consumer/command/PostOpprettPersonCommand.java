package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.pdl.OpprettPersonDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;

@RequiredArgsConstructor
public class PostOpprettPersonCommand implements Callable<OpprettPersonDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;
    private final String kilde;
    private final String token;

    @Override
    public OpprettPersonDTO call() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(PdlHeaders.NAV_PERSONIDENT, ident);
        headers.add(PdlHeaders.KILDE, kilde);
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return restTemplate.exchange(
                url + "/api/v1/bestilling/opprettperson",
                HttpMethod.POST,
                new HttpEntity<>(headers),
                OpprettPersonDTO.class
        ).getBody();
    }
}
