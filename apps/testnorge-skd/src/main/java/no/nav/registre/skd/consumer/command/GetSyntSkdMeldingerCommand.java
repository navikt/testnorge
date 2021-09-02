package no.nav.registre.skd.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@AllArgsConstructor
public class GetSyntSkdMeldingerCommand implements Callable<List<RsMeldingstype>> {

    private final String endringskode;
    private final Integer antallMeldinger;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<RsMeldingstype> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/v1/generate/tps/{endringskode}")
                                .queryParam("numToGenerate", antallMeldinger)
                                .build(antallMeldinger, endringskode)
                )
                .header("Authorization", "Bearer " + token)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }

}
