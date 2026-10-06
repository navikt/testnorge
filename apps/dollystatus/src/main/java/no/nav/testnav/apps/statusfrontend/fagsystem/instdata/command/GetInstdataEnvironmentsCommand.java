package no.nav.testnav.apps.statusfrontend.fagsystem.instdata.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.statusfrontend.fagsystem.instdata.InstdataEnvironments;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetInstdataEnvironmentsCommand implements Callable<Mono<InstdataEnvironments>> {

    private final WebClient webClient;
    private final String token;
    private final Duration timeout;

    @Override
    public Mono<InstdataEnvironments> call() {
        return webClient.get()
                .uri("/inst/api/v1/environment")
                .headers(headers -> headers.setBearerAuth(token))
                .retrieve()
                .bodyToMono(InstdataEnvironments.class)
                .switchIfEmpty(Mono.error(new IllegalStateException(
                        "Instdata returnerte ingen miljøkonfigurasjon.")))
                .timeout(timeout)
                .retryWhen(WebClientError.is5xxException());
    }
}
