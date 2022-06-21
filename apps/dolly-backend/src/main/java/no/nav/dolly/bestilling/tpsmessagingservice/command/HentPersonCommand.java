package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.PersonMiljoeDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class HentPersonCommand implements Callable<Flux<PersonMiljoeDTO>> {

    private static final String MILJOER_PARAM = "miljoer";
    private static final String PERSONER_URL = "/api/v1/personer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final List<String> miljoer;
    private final String token;

    @Override
    public Flux<PersonMiljoeDTO> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSONER_URL)
                        .queryParam(MILJOER_PARAM, miljoer)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(PersonMiljoeDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
