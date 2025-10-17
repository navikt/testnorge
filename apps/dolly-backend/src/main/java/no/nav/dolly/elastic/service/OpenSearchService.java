package no.nav.dolly.elastic.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.elastic.consumer.ElasticParamsConsumer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final ElasticParamsConsumer elasticParamsConsumer;

    public Mono<JsonNode> deleteIndex() {

        return elasticParamsConsumer.deleteIndex();
    }
}
