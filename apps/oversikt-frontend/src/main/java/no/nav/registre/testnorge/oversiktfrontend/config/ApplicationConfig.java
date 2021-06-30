package no.nav.registre.testnorge.oversiktfrontend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.frontend.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.libs.frontend.filter.SessionTimeoutCookieFilter;
import no.nav.registre.testnorge.libs.oauth2.config.SecureOAuth2FrontendConfiguration;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;
import no.nav.registre.testnorge.oversiktfrontend.config.credentials.ProfilApiServiceProperties;
import no.nav.registre.testnorge.libs.oauth2.domain.AccessScopes;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        SecureOAuth2FrontendConfiguration.class
})
@RequiredArgsConstructor
@EnableZuulProxy
public class ApplicationConfig {
    private final AccessTokenService tokenService;
    private final ProfilApiServiceProperties profilApiServiceProperties;

    @Bean
    public FilterRegistrationBean<SessionTimeoutCookieFilter> loggingFilter(){
        FilterRegistrationBean<SessionTimeoutCookieFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new SessionTimeoutCookieFilter());
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }

    @Bean
    public AddAuthorizationToRouteFilter addProfilApiAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(profilApiServiceProperties).block().getTokenValue(),
                "profil"
        );
    }

    @Bean
    public AddAuthorizationToRouteFilter addAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                () -> tokenService.generateToken(
                        new AccessScopes("api://06c9b9c1-d370-40ea-8181-2c4a6858392f/.default")
                ).block().getTokenValue(),
                "testnorge-arbeidsforhold-export-api"
        );
    }
}
