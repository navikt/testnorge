package no.nav.testnav.libs.reactivesessionsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.repository.OidcReactiveMapSessionRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.session.SessionProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.RedisTokenResolver;

import java.time.Duration;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Configuration
@EnableRedisWebSession
@Import({
        RedisTokenResolver.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        ClientRegistrationIdResolver.class,
        UserJwtExchange.class
})
@RequiredArgsConstructor
public class OicdRedisSessionConfiguration {

    private final SessionProperties sessionProperties;

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

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(
            @Value("${spring.redis.host:#{localhost}}") String host,
            @Value("${spring.redis.post:#{6379}}") Integer post
    ) {
        var redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(post);
        return redisStandaloneConfiguration;
    }

    @Bean
    public ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    public RedisSerializer<Object> springSessionRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer(objectMapper());
    }

    @Bean
    public OidcReactiveMapSessionRepository reactiveSessionRepository() {
        OidcReactiveMapSessionRepository sessionRepository = new OidcReactiveMapSessionRepository(new ConcurrentHashMap<>());
        int defaultMaxInactiveInterval = (int) (sessionProperties.getTimeout() == null
                ? Duration.ofMinutes(30)
                : sessionProperties.getTimeout()
        ).toSeconds();
        sessionRepository.setDefaultMaxInactiveInterval(defaultMaxInactiveInterval);
        log.info("Set in-memory session max inactive to {} seconds.", defaultMaxInactiveInterval);
        return sessionRepository;
    }

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        return mapper;
    }
}
