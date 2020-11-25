package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.dto.pdl.HendelseDTO;
import no.nav.registre.testnorge.person.consumer.dto.pdl.NavnDTO;
import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import no.nav.registre.testnorge.person.domain.Person;

@Slf4j
@RequiredArgsConstructor
public class PostNavnCommand implements Callable<HendelseDTO> {
    private final WebClient webClient;
    private final Person person;
    private final String kilde;
    private final String token;

    @Override
    public HendelseDTO call() {
        log.info("Legger til navn {} {}", person.getFornavn(), person.getEtternavn());
        NavnDTO body = new NavnDTO(person, kilde);
        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/bestilling/navn").build())
                .accept(MediaType.APPLICATION_JSON)
                .header(PdlHeaders.NAV_PERSONIDENT, person.getIdent())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(body), NavnDTO.class))
                .retrieve()
                .bodyToMono(HendelseDTO.class)
                .block();
    }
}
