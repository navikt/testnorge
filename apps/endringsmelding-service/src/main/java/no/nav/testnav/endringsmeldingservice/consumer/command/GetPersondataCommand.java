package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonMiljoeDTO;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetPersondataCommand implements Callable<Flux<PersonMiljoeDTO>> {

    private static final String PERSON_DATA_URL = "/api/v1/personer/{ident}";
    private static final String MILJOER = "miljoer";

    private final WebClient webClient;
    private final String ident;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<PersonMiljoeDTO> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(PERSON_DATA_URL)
                        .queryParam(MILJOER, miljoer)
                        .build(ident))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(PersonMiljoeDTO.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
