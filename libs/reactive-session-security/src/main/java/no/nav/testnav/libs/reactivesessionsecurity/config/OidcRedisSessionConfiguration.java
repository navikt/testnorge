package no.nav.testnav.libs.reactivesessionsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.RedisTokenResolver;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
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

@Configuration
@EnableRedisWebSession
@Import({
        RedisTokenResolver.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        AzureAdTokenExchange.class,
        ClientRegistrationIdResolver.class,
        UserJwtExchange.class
})
@RequiredArgsConstructor
public class OidcRedisSessionConfiguration {

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

    @Bean
    public LettuceConnectionFactory redisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(
            @Value("${spring.data.redis.host:#{localhost}}") String host,
            @Value("${spring.data.redis.post:#{6379}}") Integer post
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

    private ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        return mapper;
    }
}
