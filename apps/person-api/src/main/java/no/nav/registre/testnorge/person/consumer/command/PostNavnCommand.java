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

import no.nav.registre.testnorge.person.consumer.dto.pdl.HendelseDTO;
import no.nav.registre.testnorge.person.consumer.dto.pdl.NavnDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import no.nav.registre.testnorge.person.domain.Person;

@RequiredArgsConstructor
public class PostNavnCommand implements Callable<HendelseDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final Person person;
    private final String token;

    @Override
    public HendelseDTO call() {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(PdlHeaders.NAV_PERSONIDENT, person.getIdent());
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        ResponseEntity<HendelseDTO> exchange = restTemplate.exchange(
                url + "/api/v1/bestilling/navn",
                HttpMethod.POST,
                new HttpEntity<>(new NavnDTO(person), headers),
                HendelseDTO.class
        );
        if (!exchange.getStatusCode().is2xxSuccessful()) {
            log.info("Noe gikk galt under opprett navn {}", exchange.getStatusCode());
            throw new RuntimeException();
        }
        return exchange.getBody();
    }
}
