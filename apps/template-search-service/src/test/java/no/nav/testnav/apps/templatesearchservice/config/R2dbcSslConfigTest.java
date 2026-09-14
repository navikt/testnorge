package no.nav.testnav.apps.templatesearchservice.config;

import io.r2dbc.spi.ConnectionFactoryOptions;
import io.r2dbc.spi.Option;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;

class R2dbcSslConfigTest {

    @Test
    void shouldDisableEndpointIdentificationForNaisClientCertificateConnection() {
        var builder = ConnectionFactoryOptions.builder();

        new R2dbcSslConfig().r2dbcSslCustomizer().customize(builder);

        assertThat(builder.build().getValue(Option.valueOf("sslContextBuilderCustomizer")))
                .isInstanceOf(Function.class);
    }
}
