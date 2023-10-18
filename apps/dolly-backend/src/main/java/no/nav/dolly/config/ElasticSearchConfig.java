package no.nav.dolly.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ReactiveElasticsearchConfiguration;

import java.time.Duration;

@Profile({"prod", "dev"})
@Configuration
public class ElasticSearchConfig extends ReactiveElasticsearchConfiguration {

    @Value("${OPEN_SEARCH_USERNAME}")
    private String username;

    @Value("${OPEN_SEARCH_PASSWORD}")
    private String password;

    @Value("${OPEN_SEARCH_URI}")
    private String uri;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(uri.replace("https://", ""))
                .usingSsl()
                .withBasicAuth(username, password)
                .withConnectTimeout(Duration.ofSeconds(10))
                .withSocketTimeout(Duration.ofSeconds(5))
                .build();
    }
}