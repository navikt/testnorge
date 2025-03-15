package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.DoedsmeldingResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SendDoedsmeldingCommand implements Callable<Mono<DoedsmeldingResponse>> {

    private final WebClient webClient;
    private final DoedsmeldingRequest request;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Mono<DoedsmeldingResponse> call() {
        return webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v1/personer/doedsmelding")
                        .queryParam("miljoer", miljoer)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(DoedsmeldingResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(throwable -> WebClientError.log(throwable, log));
    }

}
