package no.nav.registre.tss.consumer.rs.command;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;

import no.nav.registre.tss.consumer.rs.response.TssMessage;
import no.nav.registre.tss.domain.Samhandler;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class PostSyntTssMeldingerCommand implements Callable<Map<String, List<TssMessage>>> {

    private final List<Samhandler> samhandlere;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<Map<String, List<TssMessage>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<List<Samhandler>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Map<String, List<TssMessage>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/tss")
                                .build()
                )
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromPublisher(Mono.just(samhandlere), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .block();
    }
}
