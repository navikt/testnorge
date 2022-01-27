package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.vault.annotation.VaultPropertySource;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

import no.nav.registre.testnorge.personsearchservice.config.credentials.ElasticSearchCredentials;

import java.io.IOException;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
@VaultPropertySource(value = "azuread/prod/creds/team-dolly-lokal-app", ignoreSecretNotFound = false)
public class DevConfig extends AbstractVaultConfiguration {

    @Bean
    public RestHighLevelClient client(ElasticSearchCredentials elasticSearchCredentials) throws IOException {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getHost() + ":" + elasticSearchCredentials.getPort())
                .build();

        try (RestClients.ElasticsearchRestClient client = RestClients.create(clientConfiguration)) {
            return client.rest();
        }
    }

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        var token = System.getProperty("spring.cloud.vault.token");
        if (token == null) {
            throw new IllegalArgumentException("Påkreved property 'spring.cloud.vault.token' er ikke satt.");
        }
        return new TokenAuthentication(System.getProperty("spring.cloud.vault.token"));
    }
}