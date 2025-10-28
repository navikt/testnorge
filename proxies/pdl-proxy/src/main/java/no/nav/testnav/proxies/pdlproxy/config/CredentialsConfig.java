package no.nav.testnav.proxies.pdlproxy.config;

import no.nav.testnav.proxies.pdlproxy.CredentialsElasticHolder;
import no.nav.testnav.proxies.pdlproxy.CredentialsOpenSearchHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CredentialsConfig {

    @Value("${app.hendelse.lager.api.key}")
    private String hendelselagerApiKey;

    @Value("${app.person.aktor.api.key}")
    private String aktoerAdminApiKey;

    @Value("${app.elastic.username}")
    private String elasticUsername;

    @Value("${app.elastic.password}")
    private String elasticPassword;

    @Bean
    CredentialsElasticHolder credentialsElasticHolder() {
        return new CredentialsElasticHolder(hendelselagerApiKey, aktoerAdminApiKey, elasticUsername, elasticPassword);
    }

    @Bean
    CredentialsOpenSearchHolder credentialsOpenSearchHolder(
            @Value("${OPEN_SEARCH_URI}") String uri,
            @Value("${OPEN_SEARCH_HOST}") String host,
            @Value("${OPEN_SEARCH_PORT}") String port,
            @Value("${OPEN_SEARCH_USERNAME}") String username,
            @Value("${OPEN_SEARCH_PASSWORD}") String password) {
        return new CredentialsOpenSearchHolder(uri, host, port, username, password);
    }
}
