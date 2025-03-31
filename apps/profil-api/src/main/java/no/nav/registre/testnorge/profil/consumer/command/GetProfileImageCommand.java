package no.nav.registre.testnorge.profil.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetProfileImageCommand implements Callable<Mono<byte[]>> {

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<byte[]> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/v1.0/me/photos/240x240/$value").build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        clientResponse -> clientResponse
                                .bodyToMono(String.class)
                                .map(IllegalStateException::new)
                )
                .bodyToMono(byte[].class)
                .doOnError(e -> !e
                                .getMessage()
                                .contains("Microsoft.Fast.Profile.Core.Exception.ImageNotFoundException"),
                        WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
