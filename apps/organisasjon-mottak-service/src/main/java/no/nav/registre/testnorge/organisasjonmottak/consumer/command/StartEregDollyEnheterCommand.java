package no.nav.registre.testnorge.organisasjonmottak.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClientRequest;
import reactor.util.retry.Retry;

import java.net.ConnectException;
import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class StartEregDollyEnheterCommand implements Callable<Mono<Void>> {

    private static final String START_URL = "/ereg-dolly/{miljo}/api/start";

    private final WebClient webClient;
    private final String baseUrl;
    private final String token;
    private final String miljo;
    private final byte[] flatfil;
    private final long maxNyeForsoek;
    private final Duration ventetid;
    private final Duration svartid;

    @Override
    public Mono<Void> call() {
        return webClient
                .post()
                .uri(baseUrl + START_URL, miljo)
                .headers(WebClientHeader.bearer(token))
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .bodyValue(flatfil)
                .httpRequest(request -> request.<HttpClientRequest>getNativeRequest().responseTimeout(svartid))
                .retrieve()
                .toBodilessEntity()
                .retryWhen(Retry.fixedDelay(maxNyeForsoek, ventetid)
                        .filter(StartEregDollyEnheterCommand::kanProevesIgjen)
                        .onRetryExhaustedThrow((spec, signal) -> signal.failure()))
                .then();
    }

    private static boolean kanProevesIgjen(Throwable throwable) {
        return throwable instanceof WebClientResponseException responseException &&
                (responseException.getStatusCode().isSameCodeAs(HttpStatus.TOO_MANY_REQUESTS) ||
                        responseException.getStatusCode().is5xxServerError()) ||
                throwable instanceof WebClientRequestException requestException &&
                        requestException.getCause() instanceof ConnectException;
    }
}
