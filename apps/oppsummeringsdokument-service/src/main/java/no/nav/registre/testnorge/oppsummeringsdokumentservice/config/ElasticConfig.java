package no.nav.registre.testnorge.oppsummeringsdokumentservice.config;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import no.nav.registre.testnorge.oppsummeringsdokumentservice.config.credentials.ElasticSearchCredentials;

@Configuration
@RequiredArgsConstructor
@EnableElasticsearchRepositories(basePackages = "no.nav.registre.testnorge.oppsummeringsdokumentservice.repository")
@EnableElasticsearchAuditing
public class ElasticConfig {
    private final ElasticSearchCredentials elasticSearchCredentials;

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getHost() + ":" + elasticSearchCredentials.getPort())
                .usingSsl()
                .withBasicAuth(elasticSearchCredentials.getUsername(), elasticSearchCredentials.getPassword())
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
