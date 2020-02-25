package no.nav.freg.token.provider.oidc;

import no.nav.freg.security.oidc.utility.openid.OpenIdConnectConfig;
import no.nav.freg.security.oidc.utility.openid.OpenIdConnectProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(OpenIdConnectConfig.class)
public class OidcConfig {

    @Bean
    public OpenIdConnectProvider provider() {
        return new OpenIdConnectProvider();
    }
}
