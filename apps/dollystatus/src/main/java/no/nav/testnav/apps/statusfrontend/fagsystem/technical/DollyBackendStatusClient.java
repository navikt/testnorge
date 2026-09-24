package no.nav.testnav.apps.statusfrontend.fagsystem.technical;

import no.nav.testnav.apps.statusfrontend.config.Consumers;
import no.nav.testnav.apps.statusfrontend.config.FunctionalTestProperties.SecondBatchTechnicalStatusProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.time.Duration;

@Service
public class DollyBackendStatusClient {

    private static final Duration SHARED_RESPONSE_TTL = Duration.ofMinutes(1);

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
                .baseUrl(consumers.getTestnavDollyBackend().getUrl())
                .build();
        sharedStatusResponse = Mono.defer(this::fetchStatuses)
                .cache(SHARED_RESPONSE_TTL);
    }

    public Mono<Void> check(String consumerName) {
        return sharedStatusResponse
                .flatMap(statuses -> validate(statuses.path(consumerName)));
    }

    private Mono<JsonNode> fetchStatuses() {
        return client.get()
                .uri("/internal/status")
                .retrieve()
                .bodyToMono(JsonNode.class)
                .timeout(properties.getRequestTimeout());
    }

    private Mono<Void> validate(JsonNode services) {
        if (!services.isObject() || services.isEmpty()) {
            return Mono.error(new IllegalStateException("Teknisk status mangler."));
        }
        for (var service : services) {
            if (!"OK".equals(service.path("alive").asText())
                    || !"OK".equals(service.path("ready").asText())) {
                return Mono.error(new IllegalStateException("Teknisk status er ikke OK."));
            }
        }
        return Mono.empty();
    }
}
