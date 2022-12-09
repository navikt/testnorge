package no.nav.testnav.proxies.aareg;

import lombok.*;
import no.nav.testnav.libs.securitytokenservice.StsOidcTokenService;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StsConfig {

    @Bean(name = "preprod")
    StsOidcTokenService preprodStsOidcTokenService(PreprodConfig config) {
        return new StsOidcTokenService(config.getUrl(), config.getUsername(), config.getPassword());
    }

    @Bean(name = "test")
    public StsOidcTokenService testStsOidcTokenService(TestConfig config) {
        return new StsOidcTokenService(config.getUrl(), config.getUsername(), config.getPassword());
    }

    @Configuration
    @Getter
    @Setter
    abstract static class StsConfigurationProperties {
        private String url;
        private String username;
        private String password;
    }

    @Configuration
    @ConfigurationProperties(prefix = "sts.preprod.token.provider")
    static class PreprodConfig extends StsConfigurationProperties {
    }

    @Configuration
    @ConfigurationProperties(prefix = "sts.test.token.provider")
    static class TestConfig extends StsConfigurationProperties {
    }

}
