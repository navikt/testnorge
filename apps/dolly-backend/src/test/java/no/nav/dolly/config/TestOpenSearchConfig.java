package no.nav.dolly.config;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.testcontainers.utility.DockerImageName;

@Configuration
@Profile("test")
@EnableElasticsearchRepositories("no.nav.dolly.elastic")
public class TestOpenSearchConfig extends AbstractOpenSearchConfiguration {

    private static final DockerImageName OPENSEARCH_IMAGE = DockerImageName.parse("opensearchproject/opensearch:2.0.0");

    private static final OpensearchContainer OPENSEARCH_CONTAINER;

    static {
        OPENSEARCH_CONTAINER = new OpensearchContainer(OPENSEARCH_IMAGE);
        OPENSEARCH_CONTAINER.withEnv("DISABLE_SECURITY_PLUGIN", "true");
        OPENSEARCH_CONTAINER.withEnv("discovery.type", "single-node");
        OPENSEARCH_CONTAINER.withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m");

        OPENSEARCH_CONTAINER.start();
    }

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(OPENSEARCH_CONTAINER.getHost() + ":" + OPENSEARCH_CONTAINER.getFirstMappedPort())
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}