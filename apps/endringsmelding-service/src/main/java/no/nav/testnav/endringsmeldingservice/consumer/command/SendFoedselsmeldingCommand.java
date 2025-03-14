package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingRequest;
import no.nav.testnav.libs.data.tpsmessagingservice.v1.FoedselsmeldingResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(FoedselsmeldingResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientFilter::logErrorMessage);
    }

}
