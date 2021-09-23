package no.nav.registre.skd.consumer.command.tpsf;

import java.util.List;
import java.util.concurrent.Callable;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class GetMeldingsIdsCommand implements Callable<List<Long>> {

    private final Long gruppeId;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public List<Long> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/meldinger/{gruppeId}")
                                .build(gruppeId)
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
