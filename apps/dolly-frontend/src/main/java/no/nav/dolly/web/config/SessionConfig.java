package no.nav.dolly.web.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisClientConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.context.AbstractHttpSessionApplicationInitializer;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

@Slf4j
@Profile("prod")
@Configuration
@EnableRedisHttpSession
public class SessionConfig extends AbstractHttpSessionApplicationInitializer {

    @Bean
    public JedisConnectionFactory connectionFactory(
            RedisStandaloneConfiguration redisStandaloneConfiguration,
            JedisClientConfiguration jedisClientConfiguration
    ) {
        var jedisConnectionFactory = new JedisConnectionFactory(redisStandaloneConfiguration, jedisClientConfiguration);
        return jedisConnectionFactory;
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") Integer port
    ) {
        log.info("Setter opp redis ({}:{})...", host, port);
        return new RedisStandaloneConfiguration(host, port);
    }

    @Bean
    public JedisClientConfiguration jedisClientConfiguration(JedisPoolConfig jedisPoolConfig) {
        return JedisClientConfiguration
                .builder()
                .usePooling().poolConfig(jedisPoolConfig)
                .and()
                .connectTimeout(Duration.ZERO)
                .readTimeout(Duration.ZERO)
                .build();
    }

    @Bean
    public JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxIdle(128);
        return poolConfig;
    }


}