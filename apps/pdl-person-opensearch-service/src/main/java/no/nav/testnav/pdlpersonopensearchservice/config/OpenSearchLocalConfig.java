package no.nav.testnav.pdlpersonopensearchservice.config;

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
    @SuppressWarnings("java:S2095")
    public RestHighLevelClient opensearchClient() {

        return RestClients.create(ClientConfiguration.builder()
                        .connectedTo(uri.replace("http://", ""))
                        .withConnectTimeout(Duration.ofSeconds(10))
                        .withSocketTimeout(Duration.ofSeconds(5))
                        .build())
                .rest();
    }
}