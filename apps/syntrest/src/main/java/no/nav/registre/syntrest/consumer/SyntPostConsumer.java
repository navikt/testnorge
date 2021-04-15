package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilderFactory;
import reactor.core.publisher.Mono;
import java.net.MalformedURLException;

import no.nav.registre.syntrest.kubernetes.ApplicationManager;

/**
 * Class for synth-packages that require a post-request
 * @param <Q> Request object (Question)
 * @param <A> Response object (Answer)
 */
@Slf4j
public class SyntPostConsumer<Q, A> extends SyntConsumer {

    private final ParameterizedTypeReference<A> responseType;
    private final ParameterizedTypeReference<Q> requestType;

    public SyntPostConsumer(
            ApplicationManager applicationManager,
            String name,
            String uri,
            boolean shutdown,
            ParameterizedTypeReference<Q> requestType,
            ParameterizedTypeReference<A> responseType,
            WebClient.Builder webClientBuilder,
            UriBuilderFactory uriFactory
    ) throws MalformedURLException {
        super(applicationManager, name, uri, shutdown, webClientBuilder, uriFactory);
        this.requestType = requestType;
        this.responseType = responseType;
    }

    public A synthesizeData(Q body) throws InterruptedException, ApiException {
        startApplication();

        A response = webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(url.getPath())
                        .build())
                .body(Mono.just(body), requestType)
                .retrieve()
                .bodyToMono(responseType)
                .block();

        scheduleIfShutdown();
        return response;
    }
}
