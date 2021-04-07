package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import reactor.core.publisher.Mono;

/**
 * Class for synth-packages that require a post-request
 * @param <T> Response object
 * @param <R> Request object
 */
@Slf4j
public class SyntPostConsumer<T, R> extends SyntConsumer {

    private final Class<T> responseClass;

    public SyntPostConsumer(ApplicationManager applicationManager, String name, String uri, boolean shutdown, Class<T> responseClass) {
        super(applicationManager, name, uri, shutdown);
        this.responseClass = responseClass;
    }

    public T synthesizeData(R body, Class<R> requestClass) throws InterruptedException, ApiException {
        startApplication();

        T response = webClient.post()
                .uri(uri)
                .body(Mono.just(body), requestClass)
                .retrieve()
                .bodyToMono(responseClass)
                .block();

        scheduleIfShutdown();
        return response;
    }
}
