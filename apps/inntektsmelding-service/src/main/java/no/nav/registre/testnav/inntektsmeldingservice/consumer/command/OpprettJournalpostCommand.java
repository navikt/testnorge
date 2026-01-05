package no.nav.registre.testnav.inntektsmeldingservice.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.dokarkiv.v1.DokmotRequest;
import no.nav.testnav.libs.dto.dokarkiv.v1.DokmotResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class OpprettJournalpostCommand implements Callable<Mono<DokmotResponse>> {

    private final WebClient webClient;
    private final String token;
    private final DokmotRequest request;
    private final String navCallId;
    private final String miljo;

    @Override
    public Mono<DokmotResponse> call() {
        return webClient
                .post()
                .uri(builder -> builder.path("/dokarkiv/api/{miljo}/v1/journalpost").build(miljo))
                .header("Nav-Call-Id", navCallId)
                .headers(WebClientHeader.bearer(token))
                .acceptCharset(StandardCharsets.UTF_8)
                .body(BodyInserters.fromPublisher(Mono.just(request), DokmotRequest.class))
                .retrieve()
                .bodyToMono(DokmotResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> {
                    if (throwable instanceof WebClientResponseException exception) {
                        log.error(
                                "Feil ved opprettelse av journalpost med body: {}.",
                                exception.getResponseBodyAsString(),
                                throwable
                        );
                    } else {
                        log.error("Feil ved opprettelse av journalpost.", throwable);
                    }
                });
    }

}
