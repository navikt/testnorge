package no.nav.registre.testnorge.jenkinsbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetBEREG007LogCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String token;
    private final Long jobNumber;

    @Override
    public Mono<String> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path("/view/All/job/Start_BEREG007/{jobNumber}/logText/progressiveText")
                        .queryParam("start", 0)
                        .build(jobNumber)
                )
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(WebClientError.is5xxException());
    }

}
