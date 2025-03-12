package no.nav.testnav.libs.commands.generernavnservice.v1;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@AllArgsConstructor
public class GenererNavnCommand implements Callable<NavnDTO[]> {

    private final WebClient webClient;
    private final String accessToken;
    private Long seed;
    private final Integer antall;

    @Override
    public NavnDTO[] call() {
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/navn")
                        .queryParamIfPresent("seed", Optional.ofNullable(seed))
                        .queryParam("antall", antall)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(NavnDTO[].class)
                .retryWhen(WebClientError.is5xxException())
                .block();
    }

}
