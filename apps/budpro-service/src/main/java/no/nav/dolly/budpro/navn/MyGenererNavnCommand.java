package no.nav.dolly.budpro.navn;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.budpro.texas.TexasToken;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@AllArgsConstructor
public class MyGenererNavnCommand implements Callable<NavnDTO[]> {

    private final WebClient webClient;
    private final Mono<TexasToken> texas;
    private Long seed;
    private final Integer antall;

    @Override
    public NavnDTO[] call() {
        return texas
                .map(TexasToken::access_token)
                .flatMap(accessToken ->
                        webClient
                                .get()
                                .uri(builder -> builder
                                        .path("/api/v1/navn")
                                        .queryParamIfPresent("seed", Optional.ofNullable(seed))
                                        .queryParam("antall", antall)
                                        .build())
                                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                                .retrieve()
                                .bodyToMono(NavnDTO[].class)
                                .retryWhen(WebClientError.is5xxException()))
                .block();
    }

}
