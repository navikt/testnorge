package no.nav.testnav.pdllagreservice.consumers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdllagreservice.dto.OpensearchDocumentData;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpensearchDocumentService {

    private final OpenSearchClient client;

    @SneakyThrows
    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public void processBulk(List<OpensearchDocumentData> docs) {

        val bulkRequest = buildBulkRequest(docs);
        log.info("Bulk request: {}, {}", bulkRequest, bulkRequest.operations().size());
        val response = client.bulk(bulkRequest);
        log.info("Bulk response: {}, {} {}", response, bulkRequest.operations().size(), bulkRequest.operations().getFirst());

        if (response.errors()) {
            log.warn("bulk request failed");
            for (BulkResponseItem item : response.items()) {
                if (item.error() != null) {
                    log.error("Build update error: {}", item.error().reason());
                }
            }
            // trigger retry or error if problem persists
            throw new RuntimeException("bulk request returned failures");
        }
    }

    private BulkRequest buildBulkRequest(List<OpensearchDocumentData> documents) {
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