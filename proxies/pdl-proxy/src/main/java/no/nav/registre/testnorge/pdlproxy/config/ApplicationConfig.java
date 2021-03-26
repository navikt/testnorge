package no.nav.registre.testnorge.pdlproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.pdlproxy.filter.AddAuthorizationAndNavConsumerTokenToRouteFilter;
import no.nav.registre.testnorge.pdlproxy.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.pdlproxy.service.StsOidcTokenService;

@Configuration
@Import(ApplicationCoreConfig.class)
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    private final StsOidcTokenService tokenService;

    @Bean
    public AddAuthorizationToRouteFilter stsAddAuthorizationToRouteFilter() {
        return new AddAuthorizationToRouteFilter(
                tokenService::getToken,
                "pdl-testdata"
        );
    }

    @Bean
    public AddAuthorizationAndNavConsumerTokenToRouteFilter stsAddAuthorizationAndNavConsumerTokenToRouteFilter() {
        return new AddAuthorizationAndNavConsumerTokenToRouteFilter(
                tokenService::getToken,
                "pdl-api"
        );
    }
}
