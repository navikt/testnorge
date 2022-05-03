package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.personsearchservice.config.credentials.PdlProxyProperties;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.net.URL;

@Configuration
@Profile("local")
@RequiredArgsConstructor
public class LocalConfig {

    private final PdlProxyProperties pdlProxyProperties;

    @Bean
    @SneakyThrows
    public RestHighLevelClient client() {

        var proxy = new URL(pdlProxyProperties.getUrl());
        var clientConfiguration
                = ClientConfiguration.builder()
                .connectedTo(String.format("%s:%s", proxy.getHost(), proxy.getDefaultPort()))
                .withPathPrefix("pdl-elastic")
                .build();

        return RestClients.create(clientConfiguration).rest();
    }
}
