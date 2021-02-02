package no.nav.registre.testnorge.oppsummeringsdokumentservice.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.io.IOException;

import no.nav.registre.testnorge.libs.localdevelopment.LocalDevelopmentConfig;

@Configuration
@Profile("dev")
@Import(LocalDevelopmentConfig.class)
public class DevConfig {

    @Bean
    public RestHighLevelClient client() throws IOException {
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo("localhost:9200")
                .build();

        try (var client = RestClients.create(clientConfiguration).rest()){
            return client;
        }
    }
}
