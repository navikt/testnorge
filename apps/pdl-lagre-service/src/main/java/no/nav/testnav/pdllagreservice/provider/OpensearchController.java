package no.nav.testnav.pdllagreservice.provider;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.pdllagreservice.consumers.OpensearchParamsConsumer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/opensearch")
@RequiredArgsConstructor
public class OpensearchController {

    @Value("${opensearch.index.personer}")
    private String personerIndex;

    private final OpensearchParamsConsumer paramsConsumer;

    @DeleteMapping("/personer")
    public Mono<JsonNode> deletePersoner() {

        return paramsConsumer.deleteIndex(personerIndex)
                .doOnNext(response ->
                        log.warn("Index {} deleted response: {}", personerIndex, response.toString()));
    }
}