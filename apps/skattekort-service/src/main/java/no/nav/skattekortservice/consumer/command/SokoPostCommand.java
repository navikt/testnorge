package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.skattekortservice.dto.SokosResponse;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static org.apache.hc.core5.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class SokoPostCommand implements Callable<Mono<SokosResponse>> {

    private static final String SOKOS_URL = "/skattekort/opprettskattekort";

    private final WebClient webClient;
    private final SokosRequest request;
    private final String token;

    @Override
    public Mono<SokosResponse> call() {

        return webClient
                .post()
                .uri(SOKOS_URL)
                .header(AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(SokosResponse.class)

    }
}
