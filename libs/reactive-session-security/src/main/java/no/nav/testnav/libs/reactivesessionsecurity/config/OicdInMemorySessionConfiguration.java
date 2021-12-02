package no.nav.testnav.libs.reactivesessionsecurity.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.session.ReactiveMapSessionRepository;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

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

    private final SessionProperties sessionProperties;

    @Bean
    public ReactiveSessionRepository reactiveSessionRepository() {
        ReactiveMapSessionRepository sessionRepository = new ReactiveMapSessionRepository(new ConcurrentHashMap<>());
        int defaultMaxInactiveInterval = (int) (sessionProperties.getTimeout() == null
                ? Duration.ofMinutes(30)
                : sessionProperties.getTimeout()
        ).toSeconds();
        sessionRepository.setDefaultMaxInactiveInterval(defaultMaxInactiveInterval);
        log.info("Set in-memory session max inactive to {} seconds.", defaultMaxInactiveInterval);
        return sessionRepository;
    }

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
