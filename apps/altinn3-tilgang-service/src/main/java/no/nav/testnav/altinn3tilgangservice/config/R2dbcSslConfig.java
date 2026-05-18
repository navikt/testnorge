package no.nav.testnav.altinn3tilgangservice.config;

import io.netty.handler.ssl.SslContextBuilder;
import io.r2dbc.spi.Option;
import org.springframework.boot.r2dbc.autoconfigure.ConnectionFactoryOptionsBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class R2dbcSslConfig {

    @Bean
    public ConnectionFactoryOptionsBuilderCustomizer r2dbcSslCustomizer() {
        return builder -> builder.option(
                Option.valueOf("sslContextBuilderCustomizer"),
                (Function<SslContextBuilder, SslContextBuilder>) sslCtx ->
                        sslCtx.endpointIdentificationAlgorithm(null)
        );
    }
}
