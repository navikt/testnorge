package no.nav.registre.inst.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.freg.security.oidc.utility.openid.OpenIdConnectConfig;
import no.nav.freg.security.oidc.utility.openid.OpenIdConnectProvider;

@Configuration
@Import(OpenIdConnectConfig.class)
public class OidcConfig {

    @Bean
    public OpenIdConnectProvider provider() {
        return new OpenIdConnectProvider();
    }
}
