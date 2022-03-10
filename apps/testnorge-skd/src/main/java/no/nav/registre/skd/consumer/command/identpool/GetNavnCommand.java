package no.nav.registre.skd.consumer.command.identpool;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.skd.consumer.response.Navn;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@AllArgsConstructor
public class GetNavnCommand implements Callable<List<Navn>> {

    private static final ParameterizedTypeReference<List<Navn>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private final WebClient webClient;

    @Override
    public List<Navn> call() {

        try {
            return webClient.get()
                    .uri(builder ->
                            builder.path("/v1/fiktive-navn/tilfeldig")
                                    .queryParam("antall", 1)
                                    .build()
                    )
                    .retrieve()
                    .bodyToMono(RESPONSE_TYPE)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();

        } catch (Exception e) {
            log.error("Klarte ikke å hente navn fra Ident-pool.", e);
            return null;
        }
    }
}
