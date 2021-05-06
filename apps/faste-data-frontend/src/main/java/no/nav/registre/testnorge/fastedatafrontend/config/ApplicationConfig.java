package no.nav.registre.testnorge.fastedatafrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.fastedatafrontend.config.credentials.OrganisasjonFasteDataServiceProperties;
import no.nav.registre.testnorge.fastedatafrontend.config.credentials.OrganisasjonServiceProperties;
import no.nav.registre.testnorge.fastedatafrontend.config.credentials.ProfilApiServiceProperties;
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
    private final ProfilApiServiceProperties profilApiServiceProperties;
    private final OrganisasjonFasteDataServiceProperties organisasjonFasteDataServiceProperties;
    private final OrganisasjonServiceProperties organisasjonServiceProperties;

    @Bean
    public AddAuthorizationToRouteFilter addProfilApiAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(profilApiServiceProperties).getTokenValue(),
                "testnorge-profil-api"
        );
    }

    @Bean
    public AddAuthorizationToRouteFilter addOrganisasjonFasteDataServiceAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(organisasjonFasteDataServiceProperties).getTokenValue(),
                "testnav-organisasjon-faste-data-service"
        );
    }

    @Bean
    public AddAuthorizationToRouteFilter addOrganisasjonServiceAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(organisasjonServiceProperties).getTokenValue(),
                "testnav-organisasjon-service"
        );
    }
}