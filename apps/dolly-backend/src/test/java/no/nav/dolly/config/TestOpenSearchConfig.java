package no.nav.dolly.config;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.cat.IndicesResponse;
import org.opensearch.client.opensearch.cat.OpenSearchCatClient;
import org.opensearch.client.opensearch.cat.indices.IndicesRecord;
import org.opensearch.client.opensearch.core.BulkRequest;
import org.opensearch.client.opensearch.core.BulkResponse;
import org.opensearch.client.opensearch.core.DeleteByQueryRequest;
import org.opensearch.client.opensearch.core.DeleteByQueryResponse;
import org.opensearch.client.opensearch.core.DeleteRequest;
import org.opensearch.client.opensearch.core.DeleteResponse;
import org.opensearch.client.opensearch.core.ExistsRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.opensearch.client.opensearch.indices.OpenSearchIndicesClient;
import org.opensearch.client.transport.endpoints.BooleanResponse;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration(proxyBeanMethods = false)
public class TestOpenSearchConfig {

    @Bean
    @Primary
    public OpenSearchClient opensearchClient() throws IOException {
        OpenSearchClient client = mock(OpenSearchClient.class);
        
        OpenSearchCatClient catClient = mock(OpenSearchCatClient.class);
        IndicesResponse indicesResponse = mock(IndicesResponse.class);
        when(indicesResponse.valueBody()).thenReturn(Collections.singletonList(
                new IndicesRecord.Builder().index("bestilling").build()
        ));
        when(catClient.indices()).thenReturn(indicesResponse);
        when(client.cat()).thenReturn(catClient);
        
        OpenSearchIndicesClient indicesClient = mock(OpenSearchIndicesClient.class);
        when(indicesClient.delete(any(java.util.function.Function.class)))
                .thenReturn(new DeleteIndexResponse.Builder().acknowledged(true).build());
        when(client.indices()).thenReturn(indicesClient);
        
        BulkResponse bulkResponse = new BulkResponse.Builder()
                .errors(false)
                .items(Collections.emptyList())
                .took(1L)
                .build();
        when(client.bulk(any(BulkRequest.class))).thenReturn(bulkResponse);
        
        DeleteByQueryResponse deleteByQueryResponse = new DeleteByQueryResponse.Builder()
                .deleted(1L)
                .total(1L)
                .took(1L)
                .timedOut(false)
                .build();
        when(client.deleteByQuery(any(DeleteByQueryRequest.class))).thenReturn(deleteByQueryResponse);
        
        BooleanResponse existsResponse = new BooleanResponse(true);
        when(client.exists(any(ExistsRequest.class))).thenReturn(existsResponse);
        
        DeleteResponse deleteResponse = mock(DeleteResponse.class);
        when(client.delete(any(DeleteRequest.class))).thenReturn(deleteResponse);
        
        return client;
    }
}