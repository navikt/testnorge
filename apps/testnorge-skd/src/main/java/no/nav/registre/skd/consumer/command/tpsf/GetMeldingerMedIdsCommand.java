package no.nav.registre.skd.consumer.command.tpsf;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.skdmelding.RsMeldingstype;

@AllArgsConstructor
public class GetMeldingerMedIdsCommand implements Callable<List<RsMeldingstype>> {

    private final String ids;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<RsMeldingstype> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/meldinger")
                                .queryParam("ids", ids)
                                .build()
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();

    }
}
