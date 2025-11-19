package no.nav.testnav.dollysearchservice.config;

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

import java.net.URISyntaxException;

@Slf4j
@Configuration
@Profile("local")
@RequiredArgsConstructor
public class OpenSearchLocalConfig {

    @Value("${open.search.username}")
    private String username;

    @Value("${open.search.password}")
    private String password;

    @Value("${open.search.uri}")
    private String uri;

    @Bean
    public OpenSearchClient opensearchClient(CredentialsProvider credentialsProvider) throws URISyntaxException {

        val transportBuilder = ApacheHttpClient5TransportBuilder
                .builder(HttpHost.create(uri))
                .setMapper(new JacksonJsonpMapper())
                .build();

        return new OpenSearchClient(transportBuilder);
    }
}