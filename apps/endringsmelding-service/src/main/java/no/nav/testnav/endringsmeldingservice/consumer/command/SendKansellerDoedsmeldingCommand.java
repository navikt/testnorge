package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.PersonDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpMethod;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SendKansellerDoedsmeldingCommand implements Callable<Mono<DoedsmeldingResponse>> {

    private final WebClient webClient;
    private final PersonDTO person;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Mono<DoedsmeldingResponse> call() {
        return webClient
                .method(HttpMethod.DELETE)
                .uri(builder -> builder
                        .path("/api/v1/personer/doedsmelding")
                        .queryParam("miljoer", miljoer)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(person))
                .retrieve()
                .bodyToMono(DoedsmeldingResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

}
