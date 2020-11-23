package no.nav.registre.testnorge.libs.common.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;
import no.nav.registre.testnorge.libs.dto.person.v1.Persondatasystem;

@Slf4j
@DependencyOn("testnorge-person-api")
@RequiredArgsConstructor
public class GetPersonCommand implements Callable<PersonDTO> {
    private final WebClient webClient;
    private final String ident;
    private final String accessToken;
    private final Persondatasystem persondatasystem;
    private final String miljoe;

    @Override
    public PersonDTO call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/personer/{ident}").build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .header("persondatasystem", persondatasystem.name())
                .header("miljoe", miljoe)
                .retrieve()
                .bodyToMono(PersonDTO.class)
                .block();
    }
}
