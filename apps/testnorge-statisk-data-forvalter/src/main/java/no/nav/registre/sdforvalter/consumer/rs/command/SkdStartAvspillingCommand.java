package no.nav.registre.sdforvalter.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.response.SkdResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class SkdStartAvspillingCommand implements Callable<Mono<SkdResponse>> {

    private final WebClient webClient;
    private final String token;
    private final Long avspillergruppeId;
    private final String miljoe;

    @Override
    public Mono<SkdResponse> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/startAvspilling/{avspillergruppeId}")
                                .queryParam("miljoe", miljoe)
                                .build(avspillergruppeId)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(SkdResponse.class);
    }

}
