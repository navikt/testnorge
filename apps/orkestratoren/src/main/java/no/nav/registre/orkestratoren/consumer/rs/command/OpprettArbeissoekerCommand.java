package no.nav.registre.orkestratoren.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class OpprettArbeissoekerCommand  implements Callable<Mono<Map<String, NyeBrukereResponse>>> {

    private final SyntetiserArenaRequest request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<Map<String, NyeBrukereResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<Map<String, NyeBrukereResponse>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/generer/bruker/oppfoelging")
                                .build()
                )
                .body(request, SyntetiserArenaRequest.class)
                .header("Authorization", "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }

}
