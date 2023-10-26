package no.nav.dolly.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.opensearch.client.Request;
import org.opensearch.client.RestClient;
import org.opensearch.testcontainers.OpensearchContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;

@Slf4j
@Configuration
public class OpenSearchConfig {

    private static final DockerImageName OPENSEARCH_IMAGE = DockerImageName.parse("opensearchproject/opensearch:2.0.0");

    @Bean
    public RestClient restClient() {

        var container = new OpensearchContainer(OPENSEARCH_IMAGE);
        // Start the container. This step might take some time...
        container.start();

        try {
            var client = RestClient
                    .builder(HttpHost.create(container.getHttpHostAddress()))
                    .build();

            var response = client.performRequest(new Request("GET", "/_cluster/health"));
            log.info("OpenSearch helsesjekk, status: {}", response);

            return client;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}