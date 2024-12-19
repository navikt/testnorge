package no.nav.testnav.apps.oppsummeringsdokumentservice.config;

import org.opensearch.client.RestHighLevelClient;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

@Profile("prod")
@Configuration
public class OpenSearchConfig extends AbstractOpenSearchConfiguration {

    @Value("${open.search.username}")
    private String username;

    @Value("${open.search.password}")
    private String password;

    @Value("${open.search.uri}")
    private String uri;

    @Override
    @SuppressWarnings("java:S2095")
    public RestHighLevelClient opensearchClient() {

        return RestClients.create(ClientConfiguration.builder()
                        .connectedTo(uri.replace("https://", ""))
                        .usingSsl()
                        .withBasicAuth(username, password)
                        .withConnectTimeout(Duration.ofSeconds(10))
                        .withSocketTimeout(Duration.ofSeconds(5))
                        .build())
                .rest();
    }
}
