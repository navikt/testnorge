package no.nav.registre.orkestratoren.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class OpprettVedtakshistorikkCommand implements Callable<Mono<Map<String, List<NyttVedtakResponse>>>> {

    private static final ParameterizedTypeReference<Map<String, List<NyttVedtakResponse>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final SyntetiserArenaRequest request;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<Map<String, List<NyttVedtakResponse>>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/generer/vedtakshistorikk")
                                .build()
                )
                .body(request, SyntetiserArenaRequest.class)
                .header("Authorization", "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE);
    }
}
