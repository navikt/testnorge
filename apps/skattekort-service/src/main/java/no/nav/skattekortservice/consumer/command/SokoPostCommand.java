package no.nav.skattekortservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.skattekortservice.dto.SokosRequest;
import no.nav.skattekortservice.dto.SokosResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class SokoPostCommand implements Callable<Mono<SokosResponse>> {

    private final WebClient webClient;
    private final SokosRequest request;
    private final String token;

    @Override
    public Mono<SokosResponse> call() {

        return webClient
                .post()
                .uri()
    }
}
