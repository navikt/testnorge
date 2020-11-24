package no.nav.registre.testnorge.person.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.person.consumer.header.PdlHeaders;
import no.nav.registre.testnorge.person.domain.Person;

@Slf4j
@RequiredArgsConstructor
public class PostTagsCommand implements Callable<String> {
    private final WebClient webClient;
    private final Person person;
    private final String token;

    @Override
    public String call() {
        log.info("Legger til tags");
        return webClient.post()
                .uri(uriBuilder -> {
                            return uriBuilder.path("/api/v1/bestilling/tags")
                                    .queryParam("tags", person.getTags().toArray())
                                    .build();
                        }
                )
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(PdlHeaders.NAV_PERSONIDENT, person.getIdent())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
}