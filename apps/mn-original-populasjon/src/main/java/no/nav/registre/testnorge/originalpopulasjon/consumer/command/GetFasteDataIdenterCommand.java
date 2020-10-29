package no.nav.registre.testnorge.originalpopulasjon.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

@Slf4j
@DependencyOn("statisk-data-forvalter")
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
        } catch (HttpClientErrorException.NotFound e) {
            return null;
        }
    }
}
