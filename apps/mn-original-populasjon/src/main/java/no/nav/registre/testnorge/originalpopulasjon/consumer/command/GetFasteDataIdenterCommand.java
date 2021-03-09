package no.nav.registre.testnorge.originalpopulasjon.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

@Slf4j
@RequiredArgsConstructor
public class GetFasteDataIdenterCommand implements Callable<PersonDTO> {
    private final WebClient webClient;
    private final String ident;

    @SneakyThrows
    @Override
    public PersonDTO call() {
        log.info("Henter faste data ident {}.", ident);
        try {
            return webClient
                    .get()
                    .uri(builder -> builder
                            .path("/api/v1/person/{ident}")
                            .build(ident)
                    )
                    .retrieve()
                    .bodyToMono(PersonDTO.class)
                    .block();
        } catch (WebClientResponseException.NotFound e) {
            return null;
        }
    }
}
