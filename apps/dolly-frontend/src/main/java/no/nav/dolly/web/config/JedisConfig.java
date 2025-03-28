package no.nav.dolly.web.config;

import io.valkey.DefaultJedisClientConfig;
import io.valkey.DefaultRedisCredentials;
import io.valkey.Jedis;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.config.OidcRedisSessionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

/**
 * Usage in {@code FrontChannelLogoutController}.
 */
@Configuration
@Profile("idporten")
@Import(OidcRedisSessionConfiguration.class)
@Slf4j
class JedisConfig {

    @Bean
    Jedis jedis(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") Integer port,
            @Value("${spring.data.redis.username}") String username,
            @Value("${spring.data.redis.password") CharSequence password
    ) {
        log.info("Connecting to Jedis on {}:{}", host, port);
        var config = DefaultJedisClientConfig
                .builder()
                .credentials(new DefaultRedisCredentials(username, password))
                .build();
        return new Jedis(host, port, config);
    }

}