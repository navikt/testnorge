package no.nav.registre.testnorge.avhengighetsanalysefrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.avhengighetsanalysefrontend.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2FrontendConfiguration;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2FrontendConfiguration.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AccessTokenService tokenService;

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(
                        new AccessScopes("api://4c17d59e-ba09-4923-a917-47efebf82cc0/.default")
                ).getTokenValue(),
                "avhengighetsanalyse-service"
        );
    }
}
