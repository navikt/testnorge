package no.nav.registre.medl.consumer.rs.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class GetSyntMeldMeldingerCommand implements Callable<List<MedlSyntResponse>> {

    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<MedlSyntResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<MedlSyntResponse> call() {

        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/generate/medl/{num_to_generate}")
                                    .build(antallMeldinger)
                    )
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

        } catch (Exception e) {
            log.error("Kunne ikke hente data fra synthdata-medl.", e);
            return Collections.emptyList();
        }

    }
}
