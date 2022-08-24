package no.nav.testnav.apps.syntsykemeldingapi.consumer.command;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntsykemeldingapi.consumer.dto.SyntSykemeldingHistorikkDTO;
import no.nav.testnav.apps.syntsykemeldingapi.exception.GenererSykemeldingerException;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class PostSyntSykemeldingCommand implements Callable<Mono<HashMap<String, SyntSykemeldingHistorikkDTO>>> {

    private static final ParameterizedTypeReference<HashMap<String, SyntSykemeldingHistorikkDTO>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Map<String, String> request;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<HashMap<String, SyntSykemeldingHistorikkDTO>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/generate_sykmeldings_history_json").build()
                )
                .header("Authorization", "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> {
                    log.error("Klarte ikke å hente data fra synthdata-elsam", throwable);
                    throw new GenererSykemeldingerException("Klarte ikke å generere sykemeldinger.");
                });
    }
}
