package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.personsearchservice.config.credentials.ElasticSearchCredentials;
import no.nav.registre.testnorge.personsearchservice.config.credentials.PdlProxyProperties;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.net.URL;

@Configuration
@Profile("dev")
@RequiredArgsConstructor
public class DevConfig {

    private final ElasticSearchCredentials elasticSearchCredentials;
    private final PdlProxyProperties pdlProxyProperties;

    @Bean
    @SneakyThrows
    public RestHighLevelClient client() {

        var proxy = new URL(pdlProxyProperties.getUrl());
        ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(elasticSearchCredentials.getHost() + ":" + elasticSearchCredentials.getPort())
                .usingSsl()
                .withProxy(proxy.getHost() + ":" + proxy.getDefaultPort())
                .withPathPrefix("pdl-elastic")
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
