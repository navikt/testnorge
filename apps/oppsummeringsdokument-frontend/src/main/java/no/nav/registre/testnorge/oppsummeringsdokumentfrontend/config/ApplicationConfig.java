package no.nav.registre.testnorge.oppsummeringsdokumentfrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.core.config.AnalysisGCPAutoConfiguration;
import no.nav.registre.testnorge.oppsummeringsdokumentfrontend.config.credentials.ArbeidsforholdApiServerProperties;
import no.nav.registre.testnorge.oppsummeringsdokumentfrontend.config.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.libs.core.config.AnalysisFSSAutoConfiguration;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2FrontendConfiguration;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2FrontendConfiguration.class,
        AnalysisGCPAutoConfiguration.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AccessTokenService tokenService;
    private final ArbeidsforholdApiServerProperties properties;

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(properties).getTokenValue(),
                "oppsummeringsdokument-service"
        );
    }

}
