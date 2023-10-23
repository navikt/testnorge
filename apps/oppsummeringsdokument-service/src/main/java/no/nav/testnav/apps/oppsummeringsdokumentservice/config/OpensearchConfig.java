package no.nav.testnav.apps.oppsummeringsdokumentservice.config;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oppsummeringsdokumentservice.config.credentials.ElasticSearchCredentials;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class OpensearchConfig extends ElasticsearchConfiguration {
    private final ElasticSearchCredentials elasticSearchCredentials;

    @Override
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getEndpoints().replace("https://", ""))
                .usingSsl()
                .withBasicAuth(elasticSearchCredentials.getUsername(), elasticSearchCredentials.getPassword())
                .withConnectTimeout(Duration.ofSeconds(10))
                .withSocketTimeout(Duration.ofSeconds(5))
                .build();
    }
}
