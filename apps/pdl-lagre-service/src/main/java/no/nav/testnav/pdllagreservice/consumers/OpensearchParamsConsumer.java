package no.nav.testnav.pdllagreservice.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.pdllagreservice.consumers.command.OpensearchPutCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class OpensearchParamsConsumer {

    private final WebClient webClient;
    private final String username;
    private final String password;

    public OpensearchParamsConsumer(

            WebClient webClient,
            @Value("${OPEN_SEARCH_USERNAME}") String username,
            @Value("${OPEN_SEARCH_PASSWORD}") String password,
            @Value("${OPEN_SEARCH_URI}") String uri) {

        this.webClient = webClient
                .mutate()
                .baseUrl(uri)
                .build();
        this.username = username;
        this.password = password;
    }

    public Mono<String> oppdaterParametre(JsonNode parametere, String index) {

        log.info("OpenSearch oppdaterer indeks \"{}\" ...", index);
        return new OpensearchPutCommand(webClient, username, password, index, parametere).call();
    }
}
