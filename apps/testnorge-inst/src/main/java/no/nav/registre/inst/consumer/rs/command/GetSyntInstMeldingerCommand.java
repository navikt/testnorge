package no.nav.registre.inst.consumer.rs.command;

import java.util.List;
import java.util.concurrent.Callable;

import no.nav.registre.inst.domain.InstitusjonsoppholdV2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;


@AllArgsConstructor
public class GetSyntInstMeldingerCommand implements Callable<List<InstitusjonsoppholdV2>> {

    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<InstitusjonsoppholdV2>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };


    @Override
    public List<InstitusjonsoppholdV2>call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/api/v1/generate/inst/{num_to_generate}")
                                .build(antallMeldinger)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }

}
