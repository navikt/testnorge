package no.nav.testnav.levendearbeidsforholdansettelse.config;

import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

@Configuration
@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        InsecureJwtServerToServerConfiguration.class
})
@EnableAsync
@EnableWebFlux
public class ApplicationConfig {

    @Bean
    @Profile("prod")
    public ConnectionFactory connectionFactory() {

        return new PostgresqlConnectionFactory(PostgresqlConnectionConfiguration.builder()
                .host("${NAIS_DATABASE_TESTNAV_LEVENDE_ARBEIDSFORHOLD_TESTNAV_LEVENDE_ARBEIDSFORHOLD_HOST}")
                .port(Integer.parseInt("${NAIS_DATABASE_TESTNAV_LEVENDE_ARBEIDSFORHOLD_TESTNAV_LEVENDE_ARBEIDSFORHOLD_PORT}"))
                .database("${NAIS_DATABASE_TESTNAV_LEVENDE_ARBEIDSFORHOLD_TESTNAV_LEVENDE_ARBEIDSFORHOLD_DATABASE}")
                .username("${NAIS_DATABASE_TESTNAV_LEVENDE_ARBEIDSFORHOLD_TESTNAV_LEVENDE_ARBEIDSFORHOLD_USERNAME}")
                .password("${NAIS_DATABASE_TESTNAV_LEVENDE_ARBEIDSFORHOLD_TESTNAV_LEVENDE_ARBEIDSFORHOLD_PASSWORD}")
                .build());
    }
}

