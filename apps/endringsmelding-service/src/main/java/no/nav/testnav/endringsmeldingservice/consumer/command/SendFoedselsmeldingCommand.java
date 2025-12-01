package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.dto.tpsmessagingservice.v1.FoedselsmeldingResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class SendFoedselsmeldingCommand implements Callable<Mono<FoedselsmeldingResponse>> {

    private final WebClient webClient;
    private final FoedselsmeldingRequest request;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Mono<FoedselsmeldingResponse> call() {
        return webClient
                .post()
                .uri(builder -> builder
                        .path("/api/v1/personer/foedselsmelding")
                        .queryParam("miljoer", miljoer)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(FoedselsmeldingResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }

}
