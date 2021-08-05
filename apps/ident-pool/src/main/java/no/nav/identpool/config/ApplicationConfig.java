package no.nav.identpool.config;

import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;

import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.servletcore.config.ApplicationProperties;
import no.nav.testnav.libs.servletcore.config.LoggingWebConfig;
import no.nav.testnav.libs.servletcore.provider.InternalController;

@Configuration
@Import({InternalController.class,
        ApplicationProperties.class,
        LoggingWebConfig.class,
        FlywayConfiguration.class,
        VaultHikariConfiguration.class})
@EnableScheduling
public class ApplicationConfig {

    @Bean
    public MapperFactory mapperFactory() {
        return new DefaultMapperFactory.Builder().build();
    }

    @Bean
    public MapperFacade mapperFacade() {
        return mapperFactory().getMapperFacade();
    }
}