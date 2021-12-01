package no.nav.registre.skd.consumer.command;

import lombok.AllArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.skd.skdmelding.RsMeldingstype;

@AllArgsConstructor
public class GetSyntSkdMeldingerCommand implements Callable<Mono<List<RsMeldingstype>>> {

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final String endringskode;
    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<List<RsMeldingstype>> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/generate/tps/{endringskode}")
                                .queryParam("antall", antallMeldinger)
                                .build(endringskode)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }

}
