package no.nav.testnav.proxies.pdlproxy.config;

import no.nav.testnav.proxies.pdlproxy.CredentialsHolder;
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
    CredentialsHolder credentialsHolder() {
        return new CredentialsHolder(hendelselagerApiKey, aktoerAdminApiKey, elasticUsername, elasticPassword);
    }

}
