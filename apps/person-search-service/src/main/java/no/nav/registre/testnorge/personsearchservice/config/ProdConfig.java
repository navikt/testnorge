package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import no.nav.registre.testnorge.personsearchservice.config.credentials.ElasticSearchCredentials;


@Slf4j
@Configuration
@Profile("prod")
@RequiredArgsConstructor
public class ProdConfig {
    private final ElasticSearchCredentials elasticSearchCredentials;

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getHost() + ":" + elasticSearchCredentials.getPort())
                .usingSsl()
                .withBasicAuth(elasticSearchCredentials.getUsername(), elasticSearchCredentials.getPassword())
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

}
