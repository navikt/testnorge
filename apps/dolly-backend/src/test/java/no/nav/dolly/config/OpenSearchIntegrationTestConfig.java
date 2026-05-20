package no.nav.dolly.config;

import lombok.val;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.OpenSearchTransport;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.opensearch.testcontainers.OpenSearchContainer;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.testcontainers.utility.DockerImageName;

import java.net.URISyntaxException;
import java.time.Duration;

@TestConfiguration(proxyBeanMethods = false)
public class OpenSearchIntegrationTestConfig {

    public static final OpenSearchContainer<?> OPENSEARCH_CONTAINER;
    private static final DockerImageName OPENSEARCH_IMAGE = DockerImageName.parse("opensearchproject/opensearch:2.19.1");

    static {
        OPENSEARCH_CONTAINER = new OpenSearchContainer<>(OPENSEARCH_IMAGE)
                .withEnv("plugins.security.disabled", "true")
                .withEnv("discovery.type", "single-node")
                .withEnv("OPENSEARCH_JAVA_OPTS", "-Xms256m -Xmx256m")
                .withEnv("bootstrap.memory_lock", "false")
                .withStartupTimeout(Duration.ofMinutes(5));
        OPENSEARCH_CONTAINER.start();
    }

    @Bean
    @Primary
    public OpenSearchClient opensearchClient() {
        HttpHost host;
        try {
            host = HttpHost.create("http://" + OPENSEARCH_CONTAINER.getHost() + ":" + OPENSEARCH_CONTAINER.getFirstMappedPort());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid OpenSearch host URI", e);
        }
        val credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host),
                new UsernamePasswordCredentials("admin", "admin".toCharArray()));

        val builder = ApacheHttpClient5TransportBuilder.builder(host);
        builder.setHttpClientConfigCallback(httpClientBuilder -> {
            val tlsStrategy = ClientTlsStrategyBuilder.create()
                    .buildAsync();

            val connectionManager = PoolingAsyncClientConnectionManagerBuilder
                    .create()
                    .setTlsStrategy(tlsStrategy)
                    .build();

            return httpClientBuilder
                    .setDefaultCredentialsProvider(credentialsProvider)
                    .setConnectionManager(connectionManager);
        });

        final OpenSearchTransport transport = builder.build();
        return new OpenSearchClient(transport);
    }
}

