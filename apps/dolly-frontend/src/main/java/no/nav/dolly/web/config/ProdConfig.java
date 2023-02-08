package no.nav.dolly.web.config;

import no.nav.testnav.libs.reactivesessionsecurity.config.OicdRedisSessionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import redis.clients.jedis.Jedis;

@Configuration
@Profile({ "prod", "dev" })
@Import({
        OicdRedisSessionConfiguration.class
})
public class ProdConfig {

    @Bean
    public Jedis jedis(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") Integer port
    ) {
        return new Jedis(host, port);
    }
}