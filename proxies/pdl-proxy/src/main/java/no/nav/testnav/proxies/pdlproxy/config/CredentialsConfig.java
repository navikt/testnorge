package no.nav.testnav.proxies.pdlproxy.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.proxies.pdlproxy.CredentialsHolder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class CredentialsConfig {

    @Value("${app.hendelse.lager.api.key}")
    private String hendelselagerApiKey;

    @Value("${app.person.aktor.admin.api}")
    private String aktoerAdminApiKey;

    @Value("${app.elastic.username}")
    private String elasticUsername;

    @Value("${app.elastic.password}")
    private String elasticPassword;

    @Value("${hendelse.lager.api.key}")
    private String oldHendelselagerApiKey;

    @Value("${person.aktor.admin.api}")
    private String oldAktoerAdminApiKey;

    @Value("${elastic.username}")
    private String oldElasticUsername;

    @Value("${elastic.password}")
    private String oldElasticPassword;

    @Bean
    CredentialsHolder credentialsHolder() {
        log.info("Checking aktoerAdminApiKey: {}", (aktoerAdminApiKey.equals(oldAktoerAdminApiKey)));
        log.info("Checking hendelselagerApiKey: {}", (hendelselagerApiKey.equals(oldHendelselagerApiKey)));
        log.info("Checking elasticUsername: {}", (elasticUsername.equals(oldElasticUsername)));
        log.info("Checking elasticPassword: {}", (elasticPassword.equals(oldElasticPassword)));
        return new CredentialsHolder(hendelselagerApiKey, aktoerAdminApiKey, elasticUsername, elasticPassword);
    }

}
