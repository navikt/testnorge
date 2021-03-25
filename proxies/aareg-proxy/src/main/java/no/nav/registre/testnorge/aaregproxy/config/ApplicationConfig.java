package no.nav.registre.testnorge.aaregproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.aaregproxy.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.aaregproxy.service.StsOidcTokenService;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;

@Configuration
@Import({
        ApplicationCoreConfig.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {
    private final RestTemplateBuilder restTemplateBuilder;

    @Bean
    public StsOidcTokenService stsPreprodOidcTokenService(
            @Value("${sts.preprod.token.provider.url}") String url,
            @Value("${sts.preprod.token.provider.url}") String username,
            @Value("${sts.preprod.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(restTemplateBuilder, url, username, password);
    }

    @Bean
    public StsOidcTokenService stsTestOidcTokenService(
            @Value("${sts.test.token.provider.url}") String url,
            @Value("${sts.test.token.provider.url}") String username,
            @Value("${sts.test.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(restTemplateBuilder, url, username, password);
    }


    @Bean
    public AddAuthorizationToRouteFilter stsPreprodAddAuthorizationToRouteFilter(StsOidcTokenService stsPreprodOidcTokenService) {
        return new AddAuthorizationToRouteFilter(
                stsPreprodOidcTokenService::getToken,
                "modapp-q0",
                "modapp-q1",
                "modapp-q2",
                "modapp-q3",
                "modapp-q4",
                "modapp-q5",
                "modapp-q6",
                "modapp-q8"
        );
    }

    @Bean
    public AddAuthorizationToRouteFilter stsTestAddAuthorizationToRouteFilter(StsOidcTokenService stsTestOidcTokenService) {
        return new AddAuthorizationToRouteFilter(
                stsTestOidcTokenService::getToken,
                "modapp-t0",
                "modapp-t1",
                "modapp-t2",
                "modapp-t3",
                "modapp-t4",
                "modapp-t5",
                "modapp-t6",
                "modapp-t8"
        );
    }
}
