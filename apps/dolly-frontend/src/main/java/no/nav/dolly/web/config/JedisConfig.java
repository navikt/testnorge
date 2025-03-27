package no.nav.dolly.web.config;

import io.valkey.Jedis;
import no.nav.testnav.libs.reactivesessionsecurity.config.OidcRedisSessionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("idporten")
@Import(OidcRedisSessionConfiguration.class)
class JedisConfig {

    @Bean
    Jedis jedis(
            @Value("${spring.data.redis.host}") String host,
            @Value("${spring.data.redis.port}") Integer port
    ) {
        return new Jedis(host, port);
    }

}