package no.nav.dolly.elastic.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.consumer.command.ElasticDeleteCommand;
import no.nav.dolly.elastic.consumer.command.ElasticPutCommand;
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
            WebClient.Builder webClientBuilder,
            @Value("${OPEN_SEARCH_USERNAME:''}") String username,
            @Value("${OPEN_SEARCH_PASSWORD:''}") String password,
            @Value("${OPEN_SEARCH_URI}") String uri,
            @Value("${open.search.index}") String index) {
        webClient = webClientBuilder
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
