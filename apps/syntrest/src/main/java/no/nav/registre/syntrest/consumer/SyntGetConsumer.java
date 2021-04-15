package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilderFactory;

import java.net.MalformedURLException;
import java.net.URI;

import no.nav.registre.syntrest.kubernetes.ApplicationManager;


@Slf4j
public class SyntGetConsumer<T> extends SyntConsumer {

    private final ParameterizedTypeReference<T> responseClass;

    public SyntGetConsumer(
            ApplicationManager applicationManager,
            String name,
            String uri,
            boolean shutdown,
            ParameterizedTypeReference<T> responseType,
            UriBuilderFactory uriFactory,
            WebClient.Builder webClientBuilder
    ) throws MalformedURLException {
        super(applicationManager, name, uri, shutdown, webClientBuilder, uriFactory);
        this.responseClass = responseType;
    }

    public synchronized T synthesizeData(String path) throws InterruptedException, ApiException {
        startApplication();
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseClass)
                .block();
        scheduleIfShutdown();
        return response;
    }

    public synchronized T synthesizeData(String path, String queries) throws InterruptedException, ApiException {
        startApplication();
        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(path)
                        .query(queries)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseClass)
                .block();
        scheduleIfShutdown();
        return response;
    }
}
