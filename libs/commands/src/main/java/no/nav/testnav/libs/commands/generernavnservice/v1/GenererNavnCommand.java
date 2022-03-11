package no.nav.testnav.libs.commands.generernavnservice.v1;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.generernavnservice.v1.NavnDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

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
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

}
