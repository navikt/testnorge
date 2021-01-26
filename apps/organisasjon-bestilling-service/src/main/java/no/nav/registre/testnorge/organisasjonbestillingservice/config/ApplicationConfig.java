package no.nav.registre.testnorge.organisasjonbestillingservice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.AnalysisGCPAutoConfiguration;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.service.AutoAnalyseService;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2ServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        AnalysisGCPAutoConfiguration.class
})
@EnableJpaAuditing
public class ApplicationConfig {
}
