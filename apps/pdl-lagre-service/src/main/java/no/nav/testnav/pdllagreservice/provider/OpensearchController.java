package no.nav.testnav.pdllagreservice.provider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/opensearch")
@RequiredArgsConstructor
public class OpensearchController {

    @Value("${opensearch.index.personer}")
    private String personerIndex;

    @Value("${opensearch.index.adresser}")
    private String adresserIndex;

    private final OpenSearchClient openSearchClient;

    @DeleteMapping("/personer")
    public String deletePersoner() throws IOException {

        return deleteIndex(personerIndex);
    }

    @DeleteMapping("/adresser")
    public String deleteAdresser() throws IOException {

        return deleteIndex(adresserIndex);
    }

    private String deleteIndex(String index) throws IOException {

        val existIndex = openSearchClient.indices().exists(i -> i.index(index));
        if (existIndex.value()) {

            log.warn("Deleting Index {}", index);
            openSearchClient.indices().delete(i -> i.index(index));
            return "Index " + index + " slettet";

        } else {
            log.warn("Index {} does not exist", index);
            return "Index " + index + " eksisterer ikke";
        }
    }
}