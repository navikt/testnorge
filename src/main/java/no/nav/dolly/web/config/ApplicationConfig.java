package no.nav.dolly.web.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import no.nav.dolly.web.config.filters.AddAuthorizationToRouteFilter;
import no.nav.dolly.web.security.TokenService;
import no.nav.dolly.web.security.domain.AccessScopes;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependenciesOn;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Configuration
@RequiredArgsConstructor
@DependenciesOn({
        @DependencyOn("dolly-backend"),
        @DependencyOn("testnorge-profil-api"),
        @DependencyOn("testnorge-varslinger-api"),
        @DependencyOn("organisasjon-forvalter"),
        @DependencyOn("testnav-miljoer-service"),
        @DependencyOn("udi-stub-dev")
})
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

    @Bean
    public AddAuthorizationToRouteFilter organisasjonForvalterAddAuthorizationToRouteFilter() {
        return createFilterFrom("organisasjon-forvalter");
    }

    @Bean
    public AddAuthorizationToRouteFilter miljoerServiceAddAuthorizationToRouteFilter() {
        return createFilterFrom("testnav-miljoer-service");
    }

    @Bean
    public AddAuthorizationToRouteFilter udiStubAddAuthorizationToRouteFilter() {
        return createFilterFrom("udi-stub");
    }

    @Bean
    public AddAuthorizationToRouteFilter personSearchAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.getAccessToken(new AccessScopes("dev-fss.dolly.person-search-service")).getTokenValue(),
                "person-search-service"
        );
    }




    private AddAuthorizationToRouteFilter createFilterFrom(String route) {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.getAccessToken(new AccessScopes(properties.get(route))).getTokenValue(),
                route
        );
    }

    @Bean
    public FilterRegistrationBean<SessionTimeoutCookieFilter> loggingFilter(){
        FilterRegistrationBean<SessionTimeoutCookieFilter> registrationBean
                = new FilterRegistrationBean<>();

        registrationBean.setFilter(new SessionTimeoutCookieFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
