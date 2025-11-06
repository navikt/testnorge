package no.nav.testnav.pdlpersonopensearchservice.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.BulkRequest;
import co.elastic.clients.elasticsearch.core.bulk.BulkResponseItem;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ElasticDocumentService {

    private final ElasticsearchClient client;

    @SneakyThrows
    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public void processBulk(List<ElasticDocumentData> docs) {
        val bulkRequest = BuildBulkRequest(docs);
        val response = client.bulk(bulkRequest);

        if (response.errors()) {
            log.warn("Elastic bulk request failed");
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    log.error("Build update error: {}", item.error().reason());
                }
            }
            // trigger retry or error if problem persists
            throw new RuntimeException("Elastic bulk request returned failures");
        }
    }


    private BulkRequest BuildBulkRequest(List<ElasticDocumentData> documents) {
        BulkRequest.Builder builder = new BulkRequest.Builder();

        for (val doc : documents) {
            builder.operations(op -> {
                        if (doc.isTombstone()) {
                            op.delete(del -> del
                                    .index(doc.getIndex())
                                    .id(doc.getIdentifikator()));
                        } else {
                            op.index(up -> up
                                    .index(doc.getIndex())
                                    .id(doc.getIdentifikator())
                                    .document(doc.getDokumentAsMap()));
                        }
                        return op;
                    }
            );
        }

        return builder.build();
    }
}