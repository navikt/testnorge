package no.nav.dolly.budpro.identities;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
public class IdentityProviderServiceConfig {

    @Bean
    @Profile("local | test")
    IdentityProviderService localIdentityProviderService() {
        log.info("Using " + LocalIdentityProviderService.class.getSimpleName());
        return new LocalIdentityProviderService();
    }

    @Bean
    @Profile("!local & !test")
    IdentityProviderService lookupIdentityProviderService() {
        log.info("Using " + LookupIdentityProviderService.class.getSimpleName());
        return new LookupIdentityProviderService();
    }

}
