package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.vault.annotation.VaultPropertySource;

import no.nav.registre.testnorge.personsearchservice.config.credentials.ElasticSearchCredentials;

@Configuration
@Profile("dev")
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
@RequiredArgsConstructor
public class DevConfig {

    private final ElasticSearchCredentials elasticSearchCredentials;

    @Bean
    public RestHighLevelClient client() {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getHost() + ":" + elasticSearchCredentials.getPort())
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
