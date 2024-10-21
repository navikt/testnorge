package no.nav.testnav.levendearbeidsforholdansettelse.config;

import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

import static io.r2dbc.spi.ConnectionFactoryOptions.DATABASE;
import static io.r2dbc.spi.ConnectionFactoryOptions.DRIVER;
import static io.r2dbc.spi.ConnectionFactoryOptions.HOST;
import static io.r2dbc.spi.ConnectionFactoryOptions.PASSWORD;
import static io.r2dbc.spi.ConnectionFactoryOptions.PORT;
import static io.r2dbc.spi.ConnectionFactoryOptions.USER;

@Configuration
@Import({
        CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class,
        InsecureJwtServerToServerConfiguration.class
})
@EnableAsync
@EnableWebFlux
@EnableR2dbcAuditing
@EnableR2dbcRepositories
public class ApplicationConfig {

    @Value("${config.r2dbc.driver}")
    private String driver;

    @Value("${config.r2dbc.host}")
    private String host;

    @Value("${config.r2dbc.port}")
    private int port;

    @Value("${config.r2dbc.username}")
    private String username;

    @Value("${config.r2dbc.password}")
    private String password;

    @Value("${config.r2dbc.database}")
    private String database;


    @Bean
    ConnectionFactory connectionFactory() {

        return ConnectionFactories.get(ConnectionFactoryOptions.builder()
                .option(DRIVER, driver)
                .option(HOST, host)
                .option(PORT, port)
                .option(USER, username)
                .option(PASSWORD, password)
                .option(DATABASE, database)
                .build());
    }
}

