package no.nav.testnav.apps.apptilganganalyseservice.config;

import io.r2dbc.h2.H2ConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.flywaydb.core.Flyway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import java.util.ArrayList;

import no.nav.testnav.apps.apptilganganalyseservice.repository.DocumentReadConverter;

@Configuration
@EnableR2dbcAuditing
@EnableR2dbcRepositories
@RequiredArgsConstructor
class R2DBCConfiguration extends AbstractR2dbcConfiguration {

    private final Environment env;

    @Bean
    @Profile("local")
    public ConnectionFactory connectionFactory() {
        return H2ConnectionFactory.inMemory("testdb");
    }

    @Bean(initMethod = "migrate")
    public Flyway flyway() {
        return new Flyway(Flyway.configure()
                .baselineOnMigrate(true)
                .dataSource(
                        env.getRequiredProperty("spring.flyway.url"),
                        env.getRequiredProperty("spring.flyway.username"),
                        env.getRequiredProperty("spring.flyway.password"))
        );
    }

    @Bean
    @Override
    public R2dbcCustomConversions r2dbcCustomConversions() {
        var converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new DocumentReadConverter());
        return new R2dbcCustomConversions(getStoreConversions(), converterList);
    }

}