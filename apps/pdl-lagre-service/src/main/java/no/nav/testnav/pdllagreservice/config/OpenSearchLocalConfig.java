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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.net.URISyntaxException;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
@EnableElasticsearchRepositories("no.nav.dolly.elastic")
public class OpenSearchLocalConfig {

    @Bean
    public CredentialsProvider credentialsProvider(@Value("${OPEN_SEARCH_HOST}") String url,
                                            @Value("${OPEN_SEARCH_USERNAME}") String username,
                                            @Value("${OPEN_SEARCH_PASSWORD}") String password) throws URISyntaxException {
        val host = HttpHost.create(url);
        val credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(new AuthScope(host), new UsernamePasswordCredentials(username, password.toCharArray()));
        return credentialsProvider;
    }

    @Bean
    public OpenSearchClient opensearchClient(@Value("${OPEN_SEARCH_HOST}") String url, CredentialsProvider credentialsProvider) throws URISyntaxException {

        val build = ApacheHttpClient5TransportBuilder
                .builder(HttpHost.create(url))
                .setMapper(new JacksonJsonpMapper())
                .setHttpClientConfigCallback(httpClientBuilder ->
                        httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider)
                ).build();

        return new OpenSearchClient(build);
    }
}