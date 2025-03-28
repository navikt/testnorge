package no.nav.dolly.web.config;

import io.valkey.DefaultJedisClientConfig;
import io.valkey.DefaultRedisCredentials;
import io.valkey.Jedis;
import io.valkey.util.JedisURIHelper;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivesessionsecurity.config.OidcRedisSessionConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import java.net.URI;
import java.net.URISyntaxException;

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
            @Value("${spring.data.redis.url}") String url,
            @Value("${spring.data.redis.username}") String username,
            @Value("${spring.data.redis.password") CharSequence password
    ) {
        log.info("Connecting to Jedis on {}", url);
        var uri = validate(url);
        var config = DefaultJedisClientConfig
                .builder()
                .credentials(new DefaultRedisCredentials(username, password))
                .build();
        return new Jedis(uri, config);
    }

    private static URI validate(String url)
            throws IllegalArgumentException {
        try {
            var uri = new URI(url);
            if (!JedisURIHelper.isValid(uri)) {
                throw new URISyntaxException(url, "Jedis does not accept URI %s".formatted(url));
            }
            return uri;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

}