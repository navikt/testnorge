package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Import({
    CoreConfig.class,
    DevConfig.class,
    SecurityConfig.class
})
@Configuration
public class SecurityConfig {

    @Bean(name = "q")
    public StsOidcTokenService stsPreprodOidcTokenService(
        @Value("${sts.preprod.token.provider.url}") String url,
        @Value("${sts.preprod.token.provider.username}") String username,
        @Value("${sts.preprod.token.provider.password}") String password) {

        return new StsOidcTokenService(url, username, password);
    }

    @Bean(name = "t")
    public StsOidcTokenService stsTestOidcTokenService(
        @Value("${sts.test.token.provider.url}") String url,
        @Value("${sts.test.token.provider.username}") String username,
        @Value("${sts.test.token.provider.password}") String password) {

        return new StsOidcTokenService(url, username, password);
    }

}
