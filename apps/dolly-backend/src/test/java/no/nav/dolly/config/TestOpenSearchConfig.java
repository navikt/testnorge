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

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {

        var container = new OpensearchContainer(OPENSEARCH_IMAGE);
        // Start the container. This step might take some time...
        container.start();
        assert (container.isRunning());

        final ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(container.getHost() + ":" + container.getFirstMappedPort())
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}