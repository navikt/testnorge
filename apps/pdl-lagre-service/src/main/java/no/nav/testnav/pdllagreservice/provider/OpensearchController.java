package no.nav.testnav.pdllagreservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/opensearch")
@RequiredArgsConstructor
public class OpensearchController {

    @Value("${opensearch.index.personer}")
    private String personerIndex;

    private final OpenSearchClient openSearchClient;

    @DeleteMapping("/personer")
    public String  deletePersoner() throws IOException {

         val deleteIndex = new DeleteIndexRequest.Builder()
                .index(personerIndex)
                .build();
        val response =  openSearchClient.indices().delete(deleteIndex);

        if (response.acknowledged()) {
            return "Index deleted successfully: %s".formatted(response.toJsonString());
        } else {
           return "Index deletion not acknowledged. %s".formatted(response.toJsonString());
        }
    }
}