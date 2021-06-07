package no.nav.registre.inntekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;

import no.nav.registre.inntekt.config.credentials.InntektsmeldingServiceProperties;
import no.nav.registre.inntekt.filter.AddAuthorizationToRouteFilter;
import no.nav.registre.testnorge.libs.core.util.VaultUtil;
import no.nav.registre.testnorge.libs.oauth2.service.AccessTokenService;

@EnableZuulProxy
@SpringBootApplication
public class ApplicationStarter {

    public static void main(String[] args) {
        if ("prod".equals(System.getProperty("spring.profiles.active"))) {
            VaultUtil.initCloudVaultToken();
        }

        SpringApplication.run(ApplicationStarter.class, args);
    }


    @Bean
    public AddAuthorizationToRouteFilter addAuthorizationToRouteFilter(
            AccessTokenService accessTokenService,
            InntektsmeldingServiceProperties properties
    ) {
        return new AddAuthorizationToRouteFilter(
                () -> accessTokenService.generateToken(properties).block().getTokenValue(),
                "testnav-inntektsmelding-service"
        );
    }

}