package no.nav.testnav.proxies.pdlproxy.config;

import no.nav.testnav.proxies.pdlproxy.CredentialsHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CredentialsConfig {

    @Value("${hendelse.lager.api.key}")
    private String hendelselagerApiKey;

    @Value("${person.aktor.admin.api}")
    private String aktoerAdminApiKey;

    @Value("${elastic.username}")
    private String elasticUsername;

    @Value("${elastic.password}")
    private String elasticPassword;

    @Bean
    CredentialsHolder credentialsHolder() {
        return new CredentialsHolder(hendelselagerApiKey, aktoerAdminApiKey, elasticUsername, elasticPassword);
    }

}
