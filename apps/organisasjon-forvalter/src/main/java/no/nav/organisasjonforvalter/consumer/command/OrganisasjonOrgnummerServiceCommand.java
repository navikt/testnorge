package no.nav.organisasjonforvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class OrganisasjonOrgnummerServiceCommand implements Callable<Mono<String[]>> {

    private static final String NUMBER_URL = "/api/v1/orgnummer";

    private final WebClient webClient;
    private final Integer antall;
    private final String token;

    @Override
    public Mono<String[]> call() {
        return webClient
                .get()
                .uri(NUMBER_URL)
                .headers(WebClientHeader.bearer(token))
                .header("antall", antall.toString())
                .retrieve()
                .bodyToMono(String[].class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
