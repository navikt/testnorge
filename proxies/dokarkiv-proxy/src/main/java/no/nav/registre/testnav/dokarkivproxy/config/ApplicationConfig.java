package no.nav.registre.testnav.dokarkivproxy.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnav.dokarkivproxy.filter.AddAuthorizationAndNavConsumerTokenToRouteFilter;
import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.service.StsOidcTokenService;

@Configuration
@Import({
        ApplicationCoreConfig.class
})
@Profile({"dev", "prod"})
@RequiredArgsConstructor
public class ApplicationConfig {

    @Bean
    public StsOidcTokenService stsPreprodOidcTokenService(
            @Value("${sts.preprod.token.provider.url}") String url,
            @Value("${sts.preprod.token.provider.username}") String username,
            @Value("${sts.preprod.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public StsOidcTokenService stsTestOidcTokenService(
            @Value("${sts.test.token.provider.url}") String url,
            @Value("${sts.test.token.provider.username}") String username,
            @Value("${sts.test.token.provider.password}") String password
    ) {
        return new StsOidcTokenService(url, username, password);
    }

    @Bean
    public AddAuthorizationAndNavConsumerTokenToRouteFilter stsPreprodAddAuthorizationToRouteFilter(StsOidcTokenService stsPreprodOidcTokenService) {
        return new AddAuthorizationAndNavConsumerTokenToRouteFilter(
                stsPreprodOidcTokenService::getToken,
                "dokarkiv-q1",
                "dokarkiv-q2",
                "dokarkiv-q4",
                "dokarkiv-q5",
                "dokarkiv-qx"
        );
    }

    @Bean
    public AddAuthorizationAndNavConsumerTokenToRouteFilter stsTestAddAuthorizationToRouteFilter(StsOidcTokenService stsTestOidcTokenService) {
        return new AddAuthorizationAndNavConsumerTokenToRouteFilter(
                stsTestOidcTokenService::getToken,
                "dokarkiv-t0:",
                "dokarkiv-t1",
                "dokarkiv-t2",
                "dokarkiv-t3",
                "dokarkiv-t4",
                "dokarkiv-t5",
                "dokarkiv-t6",
                "dokarkiv-t13"
        );
    }
}
