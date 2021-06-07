package no.nav.registre.testnorge.oppsummeringsdokumentfrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.oppsummeringsdokumentfrontend.config.credentials.ArbeidsforholdApiServerProperties;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.frontend.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2FrontendConfiguration;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.oppsummeringsdokumentfrontend.config.credentials.ProfilApiServiceProperties;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2FrontendConfiguration.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    private final AccessTokenService tokenService;
    private final ArbeidsforholdApiServerProperties properties;
    private final ProfilApiServiceProperties profilApiServiceProperties;

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(properties).block().getTokenValue(),
                "oppsummeringsdokument-service"
        );
    }

    @Bean
    public AddAuthorizationToRouteFilter addProfilApiAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(profilApiServiceProperties).block().getTokenValue(),
                "profil"
        );
    }
}
