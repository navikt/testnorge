package no.nav.dolly.elastic.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.elastic.BestillingDokument;
import no.nav.dolly.elastic.consumer.OpenSearchConsumer;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.ExistsRequest;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private final OpenSearchConsumer openSearchConsumer;
    private final OpenSearchClient openSearchClient;
    private final ObjectMapper objectMapper;

    @Value("${open.search.index}")
    private String index;


    public Mono<String> updateIndexParams(JsonNode parametere) {

        return openSearchConsumer.updateIndexParams(parametere);
    }

    public Mono<String> deleteIndex() {

        return indexExists()
                .flatMap(exists -> {
                    if (isTrue(exists)) {

                        log.warn("Deleting Index {}", index);
                        try {
                            openSearchClient.indices().delete(i -> i.index(index));
                            return Mono.just("Indeks " + index + " slettet");
                        } catch (IOException ex) {
                            log.warn("Feilet å slette index {}, {}", index, ex.getLocalizedMessage());
                            return Mono.just("Feilet å slette index " + index);
                        }
                    } else {
                        return Mono.just("Indeks " + index + " eksisterer ikke");
                    }
                });
    }

    public Mono<Boolean> indexExists() {

        try {
            val exists = openSearchClient.indices().exists(i -> i.index(index));
            return Mono.just(exists.value());

        } catch (IOException e) {
            log.warn("Feilet ved sjekk av eksistens av index {}: {}", index, e.getLocalizedMessage());
            return Mono.just(false);
        }
    }

    public Mono<BulkResponse> saveAll(List<BestillingDokument> bestillingDokument) {

        val builder = new BulkRequest.Builder()
                .index(index);
        try {

            for (var dokument : bestillingDokument) {

                var dokumentJson = buildDokumentJson(dokument);

                if (nonNull(dokumentJson)) {

                    builder.operations(op -> op
                            .index(idx -> idx
                                    .index(index)
                                    .id(String.valueOf(dokument.getId()))
                                    .document(dokumentJson)));
                }
            }

            val response = openSearchClient.bulk(builder.build());
            if (response.errors()) {
                log.warn("Feil ved lagring av bestillinger, errors i bulk response {}", response.items().getFirst().error());
            }
            return Mono.just(response);

        } catch (Exception e) {
            log.warn("Feilet å lagre bestilling id {}, {}", bestillingDokument.getFirst().getId(), e.getLocalizedMessage());
            return Mono.empty();
        }
    }


    public Mono<IndexResponse> save(BestillingDokument bestillingDokument) {

        var dokumentJson = buildDokumentJson(bestillingDokument);

        if (nonNull(dokumentJson)) {
            try {
                return Mono.just(openSearchClient.index(new IndexRequest.Builder<>()
                        .index(index)
                        .id(String.valueOf(bestillingDokument.getId()))
                        .document(dokumentJson)
                        .build()));

            } catch (Exception e) {

                log.warn("Feilet å lagre bestilling id {}, {}", bestillingDokument.getId(), e.getLocalizedMessage());
                return Mono.empty();
            }
        } else {
            return Mono.empty();
        }
    }

    private JsonNode buildDokumentJson(BestillingDokument dokument) {

        try {
            val jsonString = objectMapper.writeValueAsString(dokument);
            return objectMapper.readTree(jsonString);

        } catch (JsonProcessingException e) {
            log.warn("Feilet å serialisere bestillingDokument id {}, {}", dokument.getId(), e.getLocalizedMessage());
            return null;
        }
    }

    public Mono<Boolean> exists(Long id) {

        try {
            val exists = openSearchClient.exists(new ExistsRequest.Builder()
                    .index(index)
                    .id(String.valueOf(id))
                    .build());

            return Mono.just(exists.value());
        } catch (IOException e) {

            log.warn("Feilet ved sjekk av eksistens i OpenSearch for id: {} {}", id, e.getLocalizedMessage());
            return Mono.just(false);
        }
    }

    public Mono<String> deleteById(Long id) {

        try {
            openSearchClient.delete(new DeleteRequest.Builder()
                    .index(index)
                    .id(String.valueOf(id))
                    .build());

            return Mono.just("Deleted bestilling id: " + id);

        } catch (IOException e) {

            log.warn("Feilet å slette bestilling id {}, {}", id, e.getLocalizedMessage());
            return Mono.just("Feilet å slette bestilling id: " + id);
        }
    }
}