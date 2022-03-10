package no.nav.registre.syntrest.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.syntrest.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSyntMeldekortMedArbeidstimerCommand implements Callable<List<String>> {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final int antall;
    private final String meldegruppe;
    private final String arbeidstimer;
    private final String token;
    private final WebClient webClient;

    @Override
    public List<String> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/meldekort/{meldegruppe}/{antall}")
                                .queryParam("arbeidstimer", arbeidstimer)
                                .build(meldegruppe, antall)
                )
                .header("Authorization", "Bearer " + token)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
