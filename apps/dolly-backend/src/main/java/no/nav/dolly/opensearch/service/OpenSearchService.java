package no.nav.dolly.opensearch.service;

import tools.jackson.core.JacksonException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.opensearch.BestillingDokument;
import no.nav.dolly.opensearch.consumer.OpenSearchConsumer;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.OpenSearchException;
import org.opensearch.client.opensearch.cat.indices.IndicesRecord;
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
import static org.apache.commons.lang3.BooleanUtils.isFalse;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpenSearchService {

    private static final String INDEKS = "Indeks \"";
    
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

                        log.warn("Sletter indeks {}", index);
                        try {
                            openSearchClient.indices().delete(i -> i.index(index));
                            return Mono.just(INDEKS + index + "\" slettet");
                        } catch (IOException | OpenSearchException ex) {
                            log.warn("Feilet å slette indeks {}, {}", index, ex.getLocalizedMessage());
                            return Mono.just("Feilet å slette indeks " + index);
                        }
                    } else {
                        return Mono.just(INDEKS + index + "\" eksisterer ikke");
                    }
                });
    }

    /**
     * For testformål kun
     *
     * @return bekreftelse/status
     */
    public Mono<String> createIndex() {

        return indexExists()
                .flatMap(exists -> {
                    if (isFalse(exists)) {

                        log.warn("Oppretter indeks {}", index);
                        try {
                            openSearchClient.indices().create(i -> i.index(index));
                            return Mono.just(INDEKS + index + "\" opprettet");
                        } catch (IOException | OpenSearchException ex) {
                            log.warn("Feilet å opprette indeks {}, {}", index, ex.getLocalizedMessage());
                            return Mono.just("Feilet å opprette indeks " + index);
                        }
                    } else {
                        return Mono.just(INDEKS + index + "\" eksisterer allerede");
                    }
                });
    }

    public Mono<Boolean> indexExists() {

        try {
            return Mono.just(openSearchClient.cat().indices().valueBody().stream()
                    .map(IndicesRecord::index)
                    .anyMatch(index::equals));

        } catch (IOException | OpenSearchException e) {
            log.warn("Feilet ved sjekk av eksistens av indeks {}: {}", index, e.getLocalizedMessage());
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
                log.warn("Feil ved lagring av bestillinger, meldinger i bulk response {}", response.items().getFirst().error());
            }
            return Mono.just(response);

        } catch (IOException | OpenSearchException e) {
            log.warn("Feilet å lagre bestilling id {}, {}",
                    (!bestillingDokument.isEmpty() ?
                    bestillingDokument.getFirst().getId() : "N/A"), e.getLocalizedMessage());
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

            } catch (IOException | OpenSearchException e) {

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

        } catch (JacksonException e) {
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
        } catch (IOException | OpenSearchException e) {

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

        } catch (IOException | OpenSearchException e) {

            log.warn("Feilet å slette bestilling id {}, {}", id, e.getLocalizedMessage());
            return Mono.just("Feilet å slette bestilling id: " + id);
        }
    }
}