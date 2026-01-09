package no.nav.dolly.web.config;

import io.valkey.DefaultJedisClientConfig;
import io.valkey.Jedis;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.AzureAdTokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.TokenXExchange;
import no.nav.testnav.libs.reactivesessionsecurity.exchange.user.UserJwtExchange;
import no.nav.testnav.libs.reactivesessionsecurity.resolver.ClientRegistrationIdResolver;
import no.nav.testnav.libs.securitycore.domain.ResourceServerType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@Profile({ "prod", "dev", "idporten" })
@EnableRedisWebSession
@Import({
        AzureAdTokenExchange.class,
        TokenXExchange.class,
        ClientRegistrationIdResolver.class,
        UserJwtExchange.class
})
class SessionConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Bean
    Jedis jedis() {
        var config = DefaultJedisClientConfig
                .builder()
                .user(username)
                .password(password)
                .ssl(true)
                .build();
        return new Jedis(host, port, config);
    }

    @Bean
    @ConditionalOnMissingBean
    TokenExchange tokenExchange(
            TokenXExchange tokenXExchange,
            AzureAdTokenExchange azureAdTokenExchange,
            ClientRegistrationIdResolver clientRegistrationIdResolver) {

        var tokenExchange = new TokenExchange(clientRegistrationIdResolver);

        tokenExchange.addExchange(ResourceServerType.AZURE_AD, azureAdTokenExchange);
        tokenExchange.addExchange(ResourceServerType.TOKEN_X, tokenXExchange);
        return tokenExchange;
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
    RedisStandaloneConfiguration redisStandaloneConfiguration() {
        var config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        config.setUsername(username);
        config.setPassword(password);
        return config;
    }

    @Bean
    @ConditionalOnMissingBean
    LettuceClientConfiguration lettuceClientConfiguration() {
        return LettuceClientConfiguration
                .builder()
                .useSsl()
                .build();
    }

    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }

    @Bean
    RedisSerializer<Object> springSessionRedisSerializer() {
        return new JdkSerializationRedisSerializer();
    }

}