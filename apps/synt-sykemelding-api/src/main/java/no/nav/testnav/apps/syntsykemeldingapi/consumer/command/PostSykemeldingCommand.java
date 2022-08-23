package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.domain.Sykemelding;
import no.nav.testnav.apps.syntsykemeldingapi.exception.GenererSykemeldingerException;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PostSykemeldingCommand implements Callable<Mono<String>> {

    private final WebClient webClient;
    private final String token;
    private final Sykemelding sykemelding;

    @SneakyThrows
    @Override
    public Mono<String> call() {
        log.info("Sender sykemelding til sykemelding-api: {}", Json.pretty(sykemelding));
        return webClient.post()
                .uri(builder ->
                        builder.path("/sykemelding/api/v1/sykemeldinger").build()
                )
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(sykemelding.toDTO()))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> {
                    log.error("Feil oppsto i oppretting av sykemelding", throwable);
                    throw new GenererSykemeldingerException("Klarte ikke opprette sykemelding for " + sykemelding.getIdent());
                });
    }
}
