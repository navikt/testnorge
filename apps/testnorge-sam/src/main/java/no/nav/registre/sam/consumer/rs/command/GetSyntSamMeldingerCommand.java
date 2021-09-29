package no.nav.registre.sam.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

@RequiredArgsConstructor
public class GetSyntSamMeldingerCommand implements Callable<List<SyntetisertSamordningsmelding>> {

    private final int antall;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<SyntetisertSamordningsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<SyntetisertSamordningsmelding> call(){
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/generate_sam/{num_to_generate}")
                                .build(antall)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
