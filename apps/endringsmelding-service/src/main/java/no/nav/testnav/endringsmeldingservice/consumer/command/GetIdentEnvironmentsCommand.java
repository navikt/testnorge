package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetIdentEnvironmentsCommand implements Callable<Flux<PersonMiljoeDTO>> {
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<PersonMiljoeDTO> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/api/v1/personer/{ident}")
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(PersonMiljoeDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
