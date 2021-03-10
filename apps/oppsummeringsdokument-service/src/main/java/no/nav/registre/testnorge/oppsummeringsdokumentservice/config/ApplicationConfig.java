package no.nav.registre.testnorge.oppsummeringsdokumentservice.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Slf4j
@Configuration
@Import(value = {
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class
})
@EnableElasticsearchRepositories(basePackages = "no.nav.registre.testnorge.oppsummeringsdokumentservice.repository")
@EnableElasticsearchAuditing
@RequiredArgsConstructor
public class ApplicationConfig {
}
