package no.nav.testnav.apps.templatesearchservice.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.r2dbc.spi.Option;
import org.springframework.boot.r2dbc.autoconfigure.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.function.Function;

@Profile("prod")
@Configuration
public class R2dbcSslConfig {

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer r2dbcSslCustomizer() {
        return builder -> builder.option(
                Option.valueOf("sslContextBuilderCustomizer"),
                (Function<SslContextBuilder, SslContextBuilder>) sslContext ->
                        sslContext.endpointIdentificationAlgorithm(null)
        );
    }
}
