package no.nav.registre.testnorge.personsearchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import no.nav.registre.testnorge.personsearchservice.config.credentials.PdlProxyProperties;
import org.apache.http.HttpHost;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.client.RestClients;

import java.net.URL;

@Configuration
@Profile("dev")
@EnableJpaRepositories
@RequiredArgsConstructor
public class DevConfig {

    private final PdlProxyProperties pdlProxyProperties;

    @Value("${elasticsearch.client.host}")
    private String elasticHost;

    @Value("${elasticsearch.client.port}")
    private Integer elasticPort;

    @Bean
    @SneakyThrows
    public RestHighLevelClient client() {

        var proxy = new URL(pdlProxyProperties.getUrl());

        return new RestHighLevelClient(RestClient.builder(
                        new HttpHost(elasticHost, elasticPort, "http"))

                .setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder.setProxy(
                        new HttpHost(proxy.getHost(), proxy.getDefaultPort(), "https"))));
    }
}
