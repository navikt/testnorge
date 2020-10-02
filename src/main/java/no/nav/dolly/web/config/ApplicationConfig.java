package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.web.config.filters.AddAuthorizationToRouteFilter;
import no.nav.dolly.web.security.TokenService;
import no.nav.dolly.web.security.domain.AccessScopes;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final RemoteApplicationsProperties properties;
    private final TokenService tokenService;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AddAuthorizationToRouteFilter dollyBackendAddAuthorizationToRouteFilter() {
        return createFilterFrom("dolly-backend");
    }

    @Bean
    public AddAuthorizationToRouteFilter profilApiAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnorge-profil-api");
    }

    @Bean
    public AddAuthorizationToRouteFilter varslingerApiAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnorge-varslinger-api");
    }

    private AddAuthorizationToRouteFilter createFilterFrom(String route) {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.getAccessToken(new AccessScopes(properties.get(route))).getTokenValue(),
                route
        );
    }
}
