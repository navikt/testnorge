package no.nav.testnav.pdldatalagreservice.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.pdldatalagreservice.consumers.command.ElasticPutCommand;
import no.nav.testnav.pdldatalagreservice.consumers.command.ElasticDeleteCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class ElasticParamsConsumer {

    private final WebClient webClient;
    private final String username;
    private final String password;
    private final String index;

    public ElasticParamsConsumer(
            WebClient webClient,
            @Value("${open.search.username}") String username,
            @Value("${open.search.password}") String password,
            @Value("${open.search.uri}") String uri,
            @Value("${open.search.index}") String index
    ) {
        this.webClient = webClient
                .mutate()
                .baseUrl(uri)
                .build();
        this.username = username;
        this.password = password;
        this.index = index;
    }

    public Mono<String> oppdaterParametre(JsonNode parametere) {

        log.info("OpenSearch oppdaterer indeks \"{}\" ...", index);
        return new ElasticPutCommand(webClient, username, password, index, parametere).call();
    }

    public Mono<JsonNode> deleteIndex() {

        log.warn("OpenSearch sletter indeks \"{}\" ...", index);
        return new ElasticDeleteCommand(webClient, username, password, index).call();
    }
}
