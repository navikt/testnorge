package no.nav.registre.bisys.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.consumer.response.SyntetisertBidragsmelding;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class GetSyntBisysMeldingerCommand implements Callable<Mono<List<SyntetisertBidragsmelding>>> {

    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<SyntetisertBidragsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<List<SyntetisertBidragsmelding>> call() {
        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/api/v1/generate/bisys/{numToGenerate}")
                                    .build(antallMeldinger)
                    )
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE);
        } catch (Exception e){
            log.error("Klarte ikke hente syntetiske bidragsmeldinger", e);
            return Mono.just(Collections.emptyList());
        }
    }
}
