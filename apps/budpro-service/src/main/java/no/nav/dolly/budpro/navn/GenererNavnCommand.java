package no.nav.dolly.budpro.navn;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.budpro.texas.Texas;
import no.nav.dolly.budpro.texas.TexasToken;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

/**
 * Copied and modified from {@code no.nav.testnav.libs.commands.generernavnservice.v1.GenererNavnCommand}.
 */
@RequiredArgsConstructor
@AllArgsConstructor
@Slf4j
public class GenererNavnCommand implements Callable<NavnDTO[]> {

    private final WebClient webClient;
    private final Mono<TexasToken> token;
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
                .headers(Texas.bearer(token))
                .retrieve()
                .bodyToMono(NavnDTO[].class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .block();
    }

}
