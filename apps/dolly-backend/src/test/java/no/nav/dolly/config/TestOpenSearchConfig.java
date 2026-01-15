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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.testcontainers.utility.DockerImageName;

import java.net.URISyntaxException;

@Configuration
@Profile("test")
public class TestOpenSearchConfig {

    private static final DockerImageName OPENSEARCH_IMAGE = DockerImageName.parse("opensearchproject/opensearch:3");
    private static final OpenSearchContainer<?> OPENSEARCH_CONTAINER;

    static {
        OPENSEARCH_CONTAINER = new OpenSearchContainer<>(OPENSEARCH_IMAGE);
        OPENSEARCH_CONTAINER.withEnv("DISABLE_SECURITY_PLUGIN", "true");
        OPENSEARCH_CONTAINER.withEnv("plugins.security.disabled", "true");
        OPENSEARCH_CONTAINER.withEnv("discovery.type", "single-node");
        OPENSEARCH_CONTAINER.withEnv("OPENSEARCH_JAVA_OPTS", "-Xms512m -Xmx512m");

        OPENSEARCH_CONTAINER.start();
    }

    @Value( "${open.search.username}" )
    private String username;
    @Value( "${open.search.password}" )
    private String password;

    @Bean
    public OpenSearchClient opensearchClient() throws URISyntaxException {

        val host = HttpHost.create("http://" + OPENSEARCH_CONTAINER.getHost() + ":" + OPENSEARCH_CONTAINER.getFirstMappedPort());
        val credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host),
                new UsernamePasswordCredentials(username, password.toCharArray()));

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