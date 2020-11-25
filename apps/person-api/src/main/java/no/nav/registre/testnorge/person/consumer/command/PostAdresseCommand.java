package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.pdl.AdresseDTO;
import no.nav.registre.testnorge.person.consumer.dto.pdl.HendelseDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import no.nav.registre.testnorge.person.domain.Person;

@Slf4j
@RequiredArgsConstructor
public class PostAdresseCommand implements Callable<HendelseDTO> {

    private final WebClient webClient;
    private final Person person;
    private final String kilde;
    private final String token;
    private final String url;

    @Override
    public HendelseDTO call() {
        AdresseDTO body = new AdresseDTO(person, kilde);
        log.info("Legger til adresse. Gatenavn {} og husnummer {}", body.getVegadresse().getAdressenavn(), body.getVegadresse().getHusnummer());
        var a  = WebClient.builder().baseUrl(url).build();
        return a.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/bestilling/bostedsadresse").build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, person.getIdent())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(body), AdresseDTO.class))
                .retrieve()
                .bodyToMono(HendelseDTO.class)
                .block();
    }
}
