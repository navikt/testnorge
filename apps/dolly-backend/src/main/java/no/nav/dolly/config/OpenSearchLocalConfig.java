package no.nav.dolly.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.io.IOException;
import java.time.Duration;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
@EnableElasticsearchRepositories("no.nav.dolly.elastic")
public class OpenSearchLocalConfig extends AbstractOpenSearchConfiguration {

    @Value("${open.search.uri}")
    private String uri;

    @Override
    public RestHighLevelClient opensearchClient() {

        try (RestHighLevelClient client = RestClients.create(ClientConfiguration.builder()
                        .connectedTo(uri.replace("https://", ""))
                        .withConnectTimeout(Duration.ofSeconds(10))
                        .withSocketTimeout(Duration.ofSeconds(5))
                        .build())
                .rest()) {
            return client;

        } catch (IOException e) {
            log.error("Feilet å avslutte ressurs RestHighLevelClient.", e);
        }
        return null;
    }
}