package no.nav.dolly.config;

import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.testcontainers.OpenSearchContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Profile("test")
@EnableElasticsearchRepositories("no.nav.dolly.opensearch")
public class TestOpenSearchConfig extends AbstractOpenSearchConfiguration {

    private static final DockerImageName OPENSEARCH_IMAGE = DockerImageName.parse("opensearchproject/opensearch:3");

    private static final OpenSearchContainer<?> OPENSEARCH_CONTAINER;

    static {
        OPENSEARCH_CONTAINER = new OpenSearchContainer<>(OPENSEARCH_IMAGE);
        OPENSEARCH_CONTAINER.withEnv("DISABLE_SECURITY_PLUGIN", "true");
        OPENSEARCH_CONTAINER.withEnv("discovery.type", "single-node");
        OPENSEARCH_CONTAINER.withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m");

        OPENSEARCH_CONTAINER.start();
    }

    @Override
    @Bean
    public OpenSearchClient opensearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(OPENSEARCH_CONTAINER.getHost() + ":" + OPENSEARCH_CONTAINER.getFirstMappedPort())
                .build();

        return OpenSearchClient.create(clientConfiguration).rest();
    }
}