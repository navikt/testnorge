package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * Class for synth-packages that require a post-request with simple maps
 * @param <T> Response map
 * @param <R> Request map
 */
public class SyntPostMapConsumer<T, R> extends SyntConsumer {
    public SyntPostMapConsumer(ApplicationManager applicationManager, String name, String uri, boolean shutdown) {
        super(applicationManager, name, uri, shutdown);
    }
    public T synthesizeData(R requestMap) throws ApiException, InterruptedException {
        startApplication();
        var response = webClient.post()
                .uri(uri)
                .body(Mono.just(requestMap), Map.class)
                .retrieve()
                .bodyToMono(Map.class)
                .block();
        scheduleIfShutdown();

        return (T) response;
    }
}
