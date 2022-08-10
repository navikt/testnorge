package no.nav.testnav.apps.skdservice.consumer.command;

import lombok.AllArgsConstructor;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class GetMeldingsIdsCommand implements Callable<List<Long>> {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final Long gruppeId;
    private final WebClient webClient;

    @Override
    public List<Long> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/meldinger/{gruppeId}")
                                .build(gruppeId)
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
