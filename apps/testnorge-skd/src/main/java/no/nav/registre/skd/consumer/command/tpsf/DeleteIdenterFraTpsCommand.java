package no.nav.registre.skd.consumer.command.tpsf;

import lombok.AllArgsConstructor;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@AllArgsConstructor
public class DeleteIdenterFraTpsCommand implements Callable<Void> {

    private final String miljoerSomString;
    private final String identerSomString;
    private final WebClient webClient;

    @Override
    public Void call() {
        return webClient.delete()
                .uri(builder ->
                        builder.path("/v1/endringsmelding/skd/deleteFromTps")
                                .queryParam("miljoer", miljoerSomString)
                                .queryParam("identer", identerSomString)
                                .build()
                )
                .retrieve()
                .bodyToMono(Void.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
