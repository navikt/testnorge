package no.nav.registre.orkestratoren.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.testnav.libs.dto.syntvedtakshistorikkservice.v1.DagpengerResponseDTO;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class OpprettDagpengerCommand implements Callable<Mono<Map<String, List<DagpengerResponseDTO>>>> {

    private final SyntetiserArenaRequest request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<Map<String, List<DagpengerResponseDTO>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Mono<Map<String, List<DagpengerResponseDTO>>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/bruker/dagpenger")
                                .build()
                )
                .body(BodyInserters.fromPublisher(Mono.just(request), SyntetiserArenaRequest.class))
                .header("Authorization", "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }

}
