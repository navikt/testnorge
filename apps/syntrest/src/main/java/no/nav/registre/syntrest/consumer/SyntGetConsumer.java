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
    private final UriBuilderFactory uriFactory;

    public SyntGetConsumer(
            ApplicationManager applicationManager,
            String name,
            String uri,
            boolean shutdown,
            ParameterizedTypeReference<T> responseType,
            UriBuilderFactory uriFactory,
            WebClient.Builder webClientBuilder
    ) throws MalformedURLException {
        super(applicationManager, name, uri, shutdown, webClientBuilder);
        this.responseClass = responseType;
        this.uriFactory = uriFactory;
    }

    public synchronized T synthesizeData(String... parameters) throws InterruptedException, ApiException {
        startApplication();
        var path = url.getPath();
        var querys = url.getQuery();
        URI requestPath = uriFactory.builder()
                .path(path)
                .build((Object[]) parameters);

        var response = webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(requestPath.toString())
                        .query(querys)
                        .build())
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(responseClass)
                .block();
        scheduleIfShutdown();
        return response;
    }
}
