package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.exception.GenererSykemeldingerException;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.dto.sykemelding.v1.SykemeldingDTO;
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
    private final SykemeldingDTO sykemelding;

    @SneakyThrows
    @Override
    public Mono<String> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/sykemelding/api/v1/sykemeldinger").build()
                )
                .header("Authorization", "Bearer " + token)
                .body(BodyInserters.fromValue(sykemelding))
                .retrieve()
                .bodyToMono(String.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> {
                    log.error("Feil oppsto i oppretting av sykemelding", throwable);
                    throw new GenererSykemeldingerException("Klarte ikke opprette sykemelding for " + sykemelding.getPasient().getIdent());
                });
    }
}
