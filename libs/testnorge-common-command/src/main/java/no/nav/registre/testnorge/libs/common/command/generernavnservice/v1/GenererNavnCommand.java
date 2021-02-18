package no.nav.registre.testnorge.libs.common.command.generernavnservice.v1;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

@RequiredArgsConstructor
public class GenererNavnCommand implements Callable<NavnDTO[]> {
    private final WebClient webClient;
    private final String accessToken;
    private final Integer antall;

    @Override
    public NavnDTO[] call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/navn").queryParam("antall", antall).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NavnDTO[].class)
                .block();
    }

}
