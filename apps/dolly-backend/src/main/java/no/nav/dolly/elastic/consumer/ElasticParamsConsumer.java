package no.nav.dolly.elastic.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.elastic.consumer.command.ElasticPutCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ElasticParamsConsumer {

    private WebClient webClient;
    private String username;
    private String password;

    public ElasticParamsConsumer(
            WebClient.Builder webClientBuilder,
            @Value("${open.search.username}") String username,
            @Value("${open.search.password}") String password,
            @Value("${open.search.uri}") String uri) {

        webClient = webClientBuilder
                .baseUrl(uri)
                .build();
        this.username = username;
        this.password = password;
    }

    public Mono<String> oppdaterParametre(JsonNode parametere) {

        return new ElasticPutCommand(webClient, username, password, parametere).call();
    }
}
