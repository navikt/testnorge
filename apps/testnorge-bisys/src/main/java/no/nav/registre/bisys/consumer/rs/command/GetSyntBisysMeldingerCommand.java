package no.nav.registre.bisys.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class GetSyntBisysMeldingerCommand implements Callable<List<SyntetisertBidragsmelding>> {

    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<SyntetisertBidragsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<List<SyntetisertBidragsmelding>>() {
    };

    @Override
    public List<SyntetisertBidragsmelding> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/generate/bisys/{numToGenerate}")
                                .build(antallMeldinger)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }

}
