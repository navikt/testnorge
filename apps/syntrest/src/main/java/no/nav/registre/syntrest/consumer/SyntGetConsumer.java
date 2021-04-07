package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.springframework.web.util.UriBuilderFactory;

import java.net.URI;

import static org.apache.commons.lang.StringUtils.ordinalIndexOf;

@Slf4j
public class SyntGetConsumer<T> extends SyntConsumer {

    private final Class<T> responseClass;
    private final UriBuilderFactory uriFactory;

    public SyntGetConsumer(
            ApplicationManager applicationManager,
            String name,
            String uri,
            boolean shutdown,
            Class<T> responseClass,
            UriBuilderFactory uriFactory
    ) {
        super(applicationManager, name, uri, shutdown);
        this.responseClass = responseClass;
        this.uriFactory = uriFactory;
    }

    public synchronized T synthesizeData(String... parameters) throws InterruptedException, ApiException {
        startApplication();

        var scheme = "https";
        var host = uri.substring(ordinalIndexOf(uri, "/", 2) + 1, ordinalIndexOf(uri, "/", 3));
        var path = uri.substring(ordinalIndexOf(uri, "/", 3));
        URI requestUri = uriFactory.builder()
                .scheme(scheme)
                .host(host)
                .path(path).build(parameters);

        var response = webClient.get()
                .uri(requestUri)
                .retrieve()
                .bodyToMono(responseClass)
                .block();
        scheduleIfShutdown();

        return response;
    }
}
