package no.nav.registre.testnorge.personsearchservice.config;


import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.personsearchservice.config.credentials.PdlProxyProperties;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;

import java.net.URL;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@RequiredArgsConstructor
public class AppConfig {

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
