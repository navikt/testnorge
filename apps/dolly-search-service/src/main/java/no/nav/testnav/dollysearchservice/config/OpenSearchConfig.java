package no.nav.testnav.dollysearchservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.opensearch.client.OpenSearchClient;
import org.opensearch.client.RestHighLevelClient;
import org.opensearch.client.transport.aws.AwsSdk2Transport;
import org.opensearch.data.client.orhlc.AbstractOpenSearchConfiguration;
import org.opensearch.data.client.orhlc.ClientConfiguration;
import org.opensearch.data.client.orhlc.RestClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import java.time.Duration;

@Slf4j
//@Configuration
@Profile({"prod", "dev"})
@RequiredArgsConstructor
@EnableElasticsearchRepositories("no.nav.testnav.dollysearchservice")
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

//        return RestClients.create(ClientConfiguration.builder()
//                        .connectedTo("hostA", "hostB")
//                        .usingSsl()
//                        .withClientConfigurer(ClientConfiguration.ClientConfigurationCallback.)
//                        .withBasicAuth(username, password)
//                        .withConnectTimeout(Duration.ofSeconds(10))
//                        .withSocketTimeout(Duration.ofSeconds(5))
//                        .build(), ClientConfiguration.builder().build())
//                .rest();
        return null;
    }
}