package no.nav.testnav.levendearbeidsforholdansettelse.config;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import no.nav.testnav.libs.standalone.servletsecurity.config.InsecureJwtServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.reactive.config.EnableWebFlux;

@Slf4j
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

//    @Value("${config.r2dbc.driver}")
//    private String driver;
//
//    @Value("${config.r2dbc.host:null}")
//    private String host;
//
//    @Value("${config.r2dbc.protocol:null}")
//    private String protocol;
//
//    @Value("${config.r2dbc.port}")
//    private int port;
//
//    @Value("${config.r2dbc.username}")
//    private String username;
//
//    @Value("${config.r2dbc.password}")
//    private String password;
//
//    @Value("${config.r2dbc.database}")
//    private String database;
//
//
//    @Bean
//    ConnectionFactory connectionFactory() {
//
//        var factory = ConnectionFactoryOptions.builder()
//                .option(DRIVER, driver)
//                .option(PORT, port)
//                .option(USER, username)
//                .option(PASSWORD, password)
//                .option(DATABASE, database);
//
//        if (isNotBlank(host)) {
//            factory.option(HOST, host);
//        }
//        if (isNotBlank(protocol)) {
//            factory.option(PROTOCOL, protocol);
//        }
//
//        log.info("ConnectionFactory: {}", factory.build());
//        return ConnectionFactories.get(factory.build());
//    }
}