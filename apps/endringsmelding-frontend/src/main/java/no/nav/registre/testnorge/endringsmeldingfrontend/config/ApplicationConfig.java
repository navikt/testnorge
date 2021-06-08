package no.nav.registre.testnorge.endringsmeldingfrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.endringsmeldingfrontend.config.credentials.EndringsmeldingServiceProperties;
import no.nav.registre.testnorge.endringsmeldingfrontend.config.credentials.ProfilApiServiceProperties;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.frontend.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2FrontendConfiguration;
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
    private final EndringsmeldingServiceProperties endringsmeldingServiceProperties;
    private final ProfilApiServiceProperties profilApiServiceProperties;

    @Bean
    public AddAuthorizationToRouteFilter addEndringsmeldingAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(endringsmeldingServiceProperties).block().getTokenValue(),
                "endringsmelding"
        );
    }

    @Bean
    public AddAuthorizationToRouteFilter addIdenterAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(endringsmeldingServiceProperties).block().getTokenValue(),
                "identer"
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