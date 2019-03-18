package no.nav.registre.tp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.tp.database.multitenancy.MultitenantDataSource;

@Slf4j
@Configuration
public class MultitenantConfiguration {

    private List<String> databaseEnvironments = new ArrayList<>();

    private final DataSourceProperties properties;
    private final Environment environment;

    public MultitenantConfiguration(DataSourceProperties props, Environment environment) {
        this.properties = props;
        this.environment = environment;
        databaseEnvironments.add("q2");
        databaseEnvironments.add("q11");
    }

    /**
     * Defines the data source for the application
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();
        for (String env : databaseEnvironments) {
            DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create(Thread.currentThread().getContextClassLoader());
            // Assumption: The tenant database uses the same driver class
            // as the default database that you configure.
            String envPrefix = String.format("tp.%s.db.", env);
            dataSourceBuilder
                    .driverClassName(properties.getDriverClassName())
                    .url(environment.getProperty(envPrefix + "url"))
                    .username(environment.getProperty(envPrefix + "username"))
                    .password(environment.getProperty(envPrefix + "password"));

            if (properties.getType() != null) {
                dataSourceBuilder.type(properties.getType());
            }

            resolvedDataSources.put(env, dataSourceBuilder.build());
        }

        // Create the final multi-tenant source.
        // It needs a default database to connect to.
        // Make sure that the default database is actually an empty tenant database.
        // Don't use that for a regular tenant if you want things to be safe!
        MultitenantDataSource dataSource = new MultitenantDataSource();
        dataSource.setDefaultTargetDataSource(defaultDataSource());
        dataSource.setTargetDataSources(resolvedDataSources);

        // Call this to finalize the initialization of the data source.
        dataSource.afterPropertiesSet();

        return dataSource;
    }

    /**
     * Creates the default data source for the application
     *
     * @return
     */
    private DataSource defaultDataSource() {
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create(Thread.currentThread().getContextClassLoader())
                .driverClassName(properties.getDriverClassName())
                .url(properties.getUrl())
                .username(properties.getUsername())
                .password(properties.getPassword());

        if (properties.getType() != null) {
            dataSourceBuilder.type(properties.getType());
        }

        return dataSourceBuilder.build();
    }
}