package no.nav.registre.tp.config;

import com.google.common.collect.Maps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.tp.database.multitenancy.MultitenantDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MultitenantConfiguration {

    private final DataSourceProperties properties;
    private final Environment environment;

    @Value("#{'${environments}'.split(', ')}")
    private List<String> databaseEnvironments;

    /**
     * Defines the data source for the application
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = Maps.newHashMapWithExpectedSize(databaseEnvironments.size());
        for (String env : databaseEnvironments) {
            DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create(Thread.currentThread().getContextClassLoader());
            // Assumption: The tenant database uses the same driver class
            // as the default database that you configure.
            String envPrefix = String.format("tp.db.%s.", env);
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
        DataSourceBuilder<?> dataSourceBuilder = DataSourceBuilder.create(Thread.currentThread().getContextClassLoader())
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