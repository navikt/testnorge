package no.nav.testnav.libs.reactivesessionsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.reactivesessionsecurity.repository.OidcReactiveMapSessionRepository;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.InMemoryTokenResolver;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.session.ReactiveSessionRepository;
import org.springframework.session.config.annotation.web.server.EnableSpringWebSession;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@EnableSpringWebSession
@Import({
        InMemoryTokenResolver.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        ClientRegistrationIdResolver.class,
        UserJwtExchange.class
})
public class OidcInMemorySessionConfiguration {

    @Value("${spring.session.timeout:15m}")
    private Duration sessionTimeout;

    @Bean
    public ReactiveSessionRepository<?> reactiveSessionRepository() {
        OidcReactiveMapSessionRepository sessionRepository = new OidcReactiveMapSessionRepository(new ConcurrentHashMap<>());
        int defaultMaxInactiveInterval = (int) sessionTimeout.toSeconds();
        sessionRepository.setDefaultMaxInactiveInterval(defaultMaxInactiveInterval);
        log.info("Set in-memory session max inactive to {} seconds.", defaultMaxInactiveInterval);
        return sessionRepository;
    }

    @Bean
    @ConditionalOnMissingBean
    public TokenExchange tokenExchange(
            TokenXExchange tokenXExchange,
            AzureAdTokenExchange azureAdTokenExchange,
            ClientRegistrationIdResolver clientRegistrationIdResolver,
            ObjectMapper objectMapper) {

        var tokenExchange = new TokenExchange(clientRegistrationIdResolver, objectMapper);
        tokenExchange.addExchange(ResourceServerType.AZURE_AD, azureAdTokenExchange);
        tokenExchange.addExchange(ResourceServerType.TOKEN_X, tokenXExchange);

        return tokenExchange;
    }
}
