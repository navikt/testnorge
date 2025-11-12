package no.nav.testnav.pdllagreservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.hc.client5.http.auth.AuthScope;
import org.apache.hc.client5.http.auth.CredentialsProvider;
import org.apache.hc.client5.http.auth.UsernamePasswordCredentials;
import org.apache.hc.client5.http.impl.auth.BasicCredentialsProvider;
import org.apache.hc.core5.http.HttpHost;
import org.opensearch.client.json.jackson.JacksonJsonpMapper;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.transport.httpclient5.ApacheHttpClient5TransportBuilder;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URISyntaxException;
import java.time.Duration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class OpenSearchConfig {

    @Value("${OPEN_SEARCH_URI}")
    private String uri;

    @Value("${OPEN_SEARCH_USERNAME}")
    private String username;

    @Value("${OPEN_SEARCH_PASSWORD}")
    private String password;

    @Bean
    public CredentialsProvider credentialsProvider() throws URISyntaxException {

        val httpHost = HttpHost.create(uri);
        val credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(httpHost), new UsernamePasswordCredentials(username, password.toCharArray()));
        return credentialsProvider;
    }

    @Bean
    public OpenSearchClient opensearchClient(CredentialsProvider credentialsProvider) throws URISyntaxException {

        val transportBuilder = ApacheHttpClient5TransportBuilder
                .builder(HttpHost.create(uri))
                .setMapper(new JacksonJsonpMapper())
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                ).build();

        return new OpenSearchClient(transportBuilder);
    }

//    @Bean
//    public ClientConfiguration clientConfiguration() {
//        return ClientConfiguration.builder()
//                .connectedTo(uri)
//                .usingSsl()
//                .withBasicAuth(username, password)
//                .withConnectTimeout(Duration.ofSeconds(10))
//                .withSocketTimeout(Duration.ofSeconds(5))
//                .build();
//    }
}