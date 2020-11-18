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
import no.nav.registre.testnorge.libs.oauth2.domain.AzureClientCredentials;
import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2FrontendConfiguration.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AzureClientCredentials clientCredentials;
    private final OnBehalfOfGenerateAccessTokenService tokenService;

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(
                        clientCredentials,
                        new AccessScopes("api://a90b186a-6896-4a79-9462-03b8cc9c36a9/.default")
                ).getTokenValue(),
                "testnorge-avhengighetsanalyse-service"
        );
    }
}
