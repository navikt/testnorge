package no.nav.registre.inst.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import no.nav.registre.inst.util.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;


@AllArgsConstructor
public class GetSyntInstMeldingerCommand implements Callable<List<InstitusjonsoppholdV2>> {

    private static final ParameterizedTypeReference<List<InstitusjonsoppholdV2>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    @Override
    public List<InstitusjonsoppholdV2> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/generate/inst/{num_to_generate}")
                                .build(antallMeldinger)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

}
