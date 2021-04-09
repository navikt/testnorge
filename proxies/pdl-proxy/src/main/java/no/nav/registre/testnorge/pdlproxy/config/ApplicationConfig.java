package no.nav.registre.testnorge.pdlproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.service.StsOidcTokenService;
import no.nav.registre.testnorge.pdlproxy.filter.AddAuthorizationAndNavConsumerTokenToRouteFilter;
import no.nav.registre.testnorge.pdlproxy.filter.AddAuthorizationToRouteFilter;

@Configuration
@Import(ApplicationCoreConfig.class)
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public StsOidcTokenService stsOidcTokenService(
            @Value("${sts.token.provider.url}") String url,
            @Value("${sts.token.provider.username}") String username,
            @Value("${sts.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }


    @Bean
    public AddAuthorizationToRouteFilter stsAddAuthorizationToRouteFilter(StsOidcTokenService stsOidcTokenService) {
        return new AddAuthorizationToRouteFilter(
                stsOidcTokenService::getToken,
                "pdl-testdata"
        );
    }

    @Bean
    public AddAuthorizationAndNavConsumerTokenToRouteFilter stsAddAuthorizationAndNavConsumerTokenToRouteFilter(StsOidcTokenService stsOidcTokenService) {
        return new AddAuthorizationAndNavConsumerTokenToRouteFilter(
                stsOidcTokenService::getToken,
                "pdl-api"
        );
    }
}
