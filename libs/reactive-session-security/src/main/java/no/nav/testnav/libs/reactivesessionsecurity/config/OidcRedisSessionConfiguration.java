package no.nav.testnav.libs.reactivesessionsecurity.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

import java.util.Optional;

@Configuration
@EnableRedisWebSession
@Import({
        RedisTokenResolver.class,
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        ClientRegistrationIdResolver.class,
        UserJwtExchange.class
})
@RequiredArgsConstructor
@Slf4j
public class OidcRedisSessionConfiguration {

    @Bean
    @ConditionalOnMissingBean
    TokenExchange tokenExchange(
            TokenXExchange tokenXExchange,
            AzureAdTokenExchange azureAdTokenExchange,
            ClientRegistrationIdResolver clientRegistrationIdResolver,
            ObjectMapper objectMapper
    ) {
        return new TokenExchange(clientRegistrationIdResolver, objectMapper)
                .addExchange(ResourceServerType.AZURE_AD, azureAdTokenExchange)
                .addExchange(ResourceServerType.TOKEN_X, tokenXExchange);
    }

    @Bean
    LettuceConnectionFactory redisConnectionFactory(
            RedisStandaloneConfiguration redisStandaloneConfiguration,
            LettuceClientConfiguration lettuceClientConfiguration
    ) {
        return new LettuceConnectionFactory(redisStandaloneConfiguration, lettuceClientConfiguration);
    }

    @Bean
    @ConditionalOnMissingBean
    RedisStandaloneConfiguration redisStandaloneConfiguration(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") Integer port,
            @Value("${spring.data.redis.username}") String username,
            @Value("${spring.data.redis.password}") String password
    ) {
        log.info("Configuring Lettuce using {}:{}", host, port);
        var config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(RedisPassword.of(password));
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    LettuceClientConfiguration lettuceClientConfiguration(
            @Value("${spring.data.redis.ssl.enabled}") Boolean sslEnabled
    ) {
        var useSsl = Optional
                .ofNullable(sslEnabled)
                .orElse(Boolean.FALSE)
                .equals(Boolean.TRUE);
        if (!useSsl) {
            log.warn("Lettuce is NOT configured with spring.data.redis.ssl.enabled=true");
        }
        var config = LettuceClientConfiguration.builder();
        if (useSsl) {
            config.useSsl();
        }
        return config.build();
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    RedisSerializer<Object> springSessionRedisSerializer() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModules(SecurityJackson2Modules.getModules(getClass().getClassLoader()));
        return new GenericJackson2JsonRedisSerializer(objectMapper);
    }

}
