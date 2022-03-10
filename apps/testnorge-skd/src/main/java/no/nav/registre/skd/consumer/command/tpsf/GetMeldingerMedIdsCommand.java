package no.nav.registre.skd.consumer.command.tpsf;

import lombok.AllArgsConstructor;
import no.nav.registre.skd.skdmelding.RsMeldingstype;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class GetMeldingerMedIdsCommand implements Callable<List<RsMeldingstype>> {

    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final String ids;
    private final WebClient webClient;

    @Override
    public List<RsMeldingstype> call() {
        return webClient.get()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/meldinger")
                                .queryParam("ids", ids)
                                .build()
                )
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();

    }
}
