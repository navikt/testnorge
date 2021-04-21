package no.nav.registre.testnorge.generersyntameldingservice.consumer.command;

import java.util.List;
import java.util.concurrent.Callable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.domain.dto.aareg.amelding.Arbeidsforhold;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
public class PostHistorikkCommand implements Callable<List<Arbeidsforhold>> {

    private final WebClient webClient;
    private final Arbeidsforhold arbeidsforhold;
    private final String token;

    @Override
    public List<Arbeidsforhold> call() {
        try {
            var body = BodyInserters.fromPublisher(Mono.just(arbeidsforhold), Arbeidsforhold.class);

            return webClient.post()
                    .uri(builder -> builder
                            .path("/v1/generate/amelding/arbeidsforhold")
                            .queryParam("avvik", "false")
                            .queryParam("sluttdato", "false")
                            .build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .body(body)
                    .retrieve()
                    .bodyToFlux(Arbeidsforhold.class)
                    .collectList()
                    .block();
        } catch (Exception e) {
            log.error("Unexpected Rest Client Exception: " + e.getMessage());
            throw e;
        }
    }
}
