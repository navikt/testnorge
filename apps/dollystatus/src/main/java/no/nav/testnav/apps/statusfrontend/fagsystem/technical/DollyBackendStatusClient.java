package no.nav.testnav.apps.statusfrontend.fagsystem.technical;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SecondBatchTechnicalStatusProperties;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import tools.jackson.databind.JsonNode;

import java.time.Duration;

@Service
public class DollyBackendStatusClient {

    private final SecondBatchTechnicalStatusProperties properties;
    private final WebClient client;
    private final Mono<JsonNode> sharedStatusResponse;

    public DollyBackendStatusClient(
            Consumers consumers,
            SecondBatchTechnicalStatusProperties properties,
            WebClient webClient
    ) {
        this.properties = properties;
        client = webClient.mutate()
                .clientConnector(new ReactorClientHttpConnector(HttpClient.create()
                        .responseTimeout(properties.getRequestTimeout())))
                .baseUrl(consumers.getTestnavDollyBackend().getUrl())
                .build();
        var sharedResponseTtl = properties.getRetryDelay().dividedBy(2);
        sharedStatusResponse = Mono.defer(this::fetchStatuses)
                .cache(_ -> sharedResponseTtl, _ -> Duration.ZERO, () -> Duration.ZERO);
    }

    public Mono<Void> check(String consumerName) {
        return sharedStatusResponse
                .flatMap(statuses -> validate(statuses.path(consumerName)))
                .retryWhen(TechnicalStatusRetry.retryOn(
                        properties,
                        TechnicalStatusNotOkException.class::isInstance));
    }

    private Mono<JsonNode> fetchStatuses() {
        return client.get()
                .uri("/internal/status")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(properties.getRequestTimeout())
                .retryWhen(TechnicalStatusRetry.transientFailures(properties))
                .switchIfEmpty(Mono.error(new IllegalStateException("Teknisk status mangler.")));
    }

    private Mono<Void> validate(JsonNode services) {
        if (!services.isObject() || services.isEmpty()) {
            return Mono.error(new IllegalStateException("Teknisk status mangler."));
        }
        for (var service : services) {
            if (!"OK".equals(service.path("alive").asString())
                    || !"OK".equals(service.path("ready").asString())) {
                return Mono.error(new TechnicalStatusNotOkException());
            }
        }
        return Mono.empty();
    }

    private static final class TechnicalStatusNotOkException extends IllegalStateException {

        private TechnicalStatusNotOkException() {
            super("Teknisk status er ikke OK.");
        }
    }
}
