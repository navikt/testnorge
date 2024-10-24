package no.nav.testnav.levendearbeidsforholdansettelse.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.lang.NonNull;

@Configuration
@Slf4j
class R2dbcConfiguration extends AbstractR2dbcConfiguration {

    @Value("${spring.r2dbc.url}")
    private String url;

    @Bean
    @NonNull
    public ConnectionFactory connectionFactory() {
        try {
            return ConnectionFactories.get(url);
        } finally {
            log.info("Created connection factory for URL {}", url);
        }
    }

    @Bean
    @NonNull
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory factory) {
        try {
            return new R2dbcEntityTemplate(factory);
        } finally {
            log.info("Created entity template using factory {}", factory.getMetadata().getName());
        }
    }

}