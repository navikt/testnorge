package no.nav.dolly.web.config.session;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.convert.DurationStyle;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.config.ReactiveSessionRepositoryCustomizer;
import org.springframework.session.data.redis.ReactiveRedisSessionRepository;
import org.springframework.session.data.redis.config.annotation.web.server.EnableRedisWebSession;

@Configuration
@Profile({ "prod", "dev", "idporten" })
@EnableRedisWebSession
public class SessionStorageConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.username}")
    private String username;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.ssl.enabled:true}")
    private boolean ssl;

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
        var builder = LettuceClientConfiguration.builder();
        if (ssl) {
            builder.useSsl();
        }
        return builder.build();
    }

    @Bean("springSessionDefaultRedisSerializer")
    RedisSerializer<Object> springSessionDefaultRedisSerializer(
            @Value("${SESSION_ENCRYPTION_KEY:}") String sessionEncryptionKey,
            @Value("${NAIS_APP_NAME:${spring.application.name}}") String encryptionContext
    ) {
        return new EncryptingRedisSerializer(
                new JdkSerializationRedisSerializer(getClass().getClassLoader()),
                SessionEncryptionKey.fromBase64(sessionEncryptionKey),
                encryptionContext);
    }

    @Bean
    ReactiveSessionRepositoryCustomizer<ReactiveRedisSessionRepository> sessionRepositoryCustomizer(
            @Value("${spring.session.timeout:3h}") String timeout
    ) {
        var maxInactiveInterval = DurationStyle.detectAndParse(timeout);
        return repository -> {
            repository.setDefaultMaxInactiveInterval(maxInactiveInterval);
            repository.setRedisSessionMapper(new UnreadableSessionDiscardingMapper());
        };
    }
}
