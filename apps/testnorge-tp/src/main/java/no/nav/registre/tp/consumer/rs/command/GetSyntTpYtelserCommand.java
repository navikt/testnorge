package no.nav.registre.tp.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.database.models.TYtelse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetSyntTpYtelserCommand implements Callable<List<TYtelse>> {

    private final int antall;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<TYtelse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<TYtelse> call() {

        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/generate/tp/{num_to_generate}")
                                    .build(antall)
                    )
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

        } catch (Exception e) {
            log.error("Klarte ikke hente meldinger fra synthdata-tp.", e);
            return new LinkedList<>();
        }
    }
}
