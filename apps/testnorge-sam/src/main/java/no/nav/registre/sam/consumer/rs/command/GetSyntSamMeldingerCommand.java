package no.nav.registre.sam.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetSyntSamMeldingerCommand implements Callable<List<SyntetisertSamordningsmelding>> {

    private final int antall;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<SyntetisertSamordningsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<SyntetisertSamordningsmelding> call() {

        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/generate_sam/{num_to_generate}")
                                    .build(antall)
                    )
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
