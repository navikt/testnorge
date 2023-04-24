package no.nav.testnav.apps.oppsummeringsdokumentservice.config;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.oppsummeringsdokumentservice.config.credentials.ElasticSearchCredentials;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ElasticConfig extends AbstractOpenSearchConfiguration {
    private final ElasticSearchCredentials elasticSearchCredentials;

    @Override
    @Bean
    public RestHighLevelClient opensearchClient() {
        ClientConfiguration clientConfiguration = ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getEndpoints())
                .usingSsl()
                .withBasicAuth(elasticSearchCredentials.getUsername(), elasticSearchCredentials.getPassword())
                .build();

        return RestClients.create(clientConfiguration).rest();
    }

}
