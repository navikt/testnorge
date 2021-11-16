package no.nav.testnav.libs.reactivesessionsecurity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.InMemoryTokenResolver;

@Slf4j
@EnableSpringWebSession
@Import({
        InMemoryTokenResolver.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        ClientRegistrationIdResolver.class,
        UserJwtExchange.class
})
@RequiredArgsConstructor
public class OicdInMemorySessionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public TokenExchange tokenExchange(
            ClientRegistrationIdResolver clientRegistrationIdResolver,
            TokenXExchange tokenXExchange,
            AzureAdTokenExchange azureAdTokenExchange
    ) {
        var tokenExchange = new TokenExchange(clientRegistrationIdResolver);
        tokenExchange.addExchange("aad", azureAdTokenExchange);
        tokenExchange.addExchange("idporten", tokenXExchange);
        return tokenExchange;
    }
}
