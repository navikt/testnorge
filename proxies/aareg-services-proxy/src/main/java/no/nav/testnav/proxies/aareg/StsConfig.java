package no.nav.testnav.proxies.aareg;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactiveproxy.config.DevConfig;
import no.nav.testnav.libs.reactiveproxy.config.SecurityConfig;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;
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
public class StsConfig {

    @Value("${sts.preprod.token.provider.url:}") String qUrl;
    @Value("${sts.preprod.token.provider.username:}") String qUsername;
    @Value("${sts.preprod.token.provider.password:}") String qPassword;

    @Value("${sts.test.token.provider.url:}") String tUrl;
    @Value("${sts.test.token.provider.username:}") String tUsername;
    @Value("${sts.test.token.provider.password:}") String tPassword;

    @Bean(name = "q")
    StsOidcTokenService qStsOidcTokenService() {
        return new StsOidcTokenService(qUrl, qUsername, qPassword);
    }

    @Bean(name = "t")
    public StsOidcTokenService tStsOidcTokenService() {
        return new StsOidcTokenService(tUrl, tUsername, tPassword);
    }

}
