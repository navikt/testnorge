package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.AdresseDTO;
import no.nav.registre.testnorge.person.consumer.dto.HendelseDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import no.nav.registre.testnorge.person.domain.Person;

@RequiredArgsConstructor
public class PostAdresseCommand implements Callable<HendelseDTO> {

    private final RestTemplate restTemplate;
    private final String url;
    private final Person person;
    private final String token;

    @Override
    public HendelseDTO call() throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.add(PdlHeaders.NAV_PERSONIDENT, person.getIdent());
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + token);
        return restTemplate.exchange(
                url + "/api/v1/bestilling/bostedadresse",
                HttpMethod.POST,
                new HttpEntity<>(new AdresseDTO(person), headers),
                HendelseDTO.class
        ).getBody();
    }
}
