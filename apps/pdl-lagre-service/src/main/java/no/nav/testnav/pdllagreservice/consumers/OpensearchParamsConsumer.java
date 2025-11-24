package no.nav.testnav.pdllagreservice.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.pdllagreservice.consumers.command.OpensearchPutCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Slf4j
@Service
public class OpensearchParamsConsumer {

    private static final String MAPPING_URL = "_mapping";

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

    public Mono<String> oppdaterParametre(String index, boolean isMapping, JsonNode parametere) {

        log.info("OpenSearch oppdaterer indeks \"{}{}\" ...", index, isMapping ? "/" + MAPPING_URL : "");
        return new OpensearchPutCommand(webClient, Optional.ofNullable(isMapping ? MAPPING_URL : null),
                username, password, index, parametere).call();
    }
}
