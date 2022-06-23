package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.registre.testnorge.personsearchservice.config.credentials.ElasticSearchCredentials;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ElasticConfig {
    private final ElasticSearchCredentials elasticSearchCredentials;

    @Bean
    CredentialsProvider credentialsProvider() {

        val credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(elasticSearchCredentials.getUsername(),
                elasticSearchCredentials.getPassword()));

        return credentialsProvider;
    }

    @Bean
    public RestHighLevelClient highLevelClient() {

        val builder = RestClient.builder(HttpHost.create(elasticSearchCredentials.getHost()))
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                        .setConnectTimeout(5000)
                        .setSocketTimeout(60000))
                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider()));

        return new RestHighLevelClient(builder);
    }
}
