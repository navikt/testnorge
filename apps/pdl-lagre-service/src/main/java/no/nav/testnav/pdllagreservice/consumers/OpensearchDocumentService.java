package no.nav.testnav.pdllagreservice.consumers;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.testnav.pdllagreservice.dto.OpensearchDocumentData;
import org.apache.commons.lang3.BooleanUtils;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.bulk.BulkResponseItem;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.nonNull;
import static org.apache.commons.lang3.BooleanUtils.isTrue;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpensearchDocumentService {

    private final OpenSearchClient client;

    @Retryable(maxAttempts = 5, backoff = @Backoff(multiplier = 2))
    public void processBulk(List<OpensearchDocumentData> docs) {

        val bulkRequest = buildBulkRequest(docs);
        log.info("Bulk request: {}, {}", bulkRequest, bulkRequest.operations().size());
        BulkResponse response = null;
        try {
            response = client.bulk(bulkRequest);
        } catch (IOException e) {
            log.error("Feil oppstått ved lagring av bulk request {}", e.getMessage(), e);
        }
        log.info("Bulk response: {}, {} {}", response, bulkRequest.operations().size(), bulkRequest.operations().getFirst());

        if (nonNull(response) && response.errors()) {
            log.warn("Bulk request failed");
            for (BulkResponseItem item : response.items()) {
                if (nonNull(item.error())) {
                    log.warn("Bulk update error: {}", item.error().reason());
                }
            }
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