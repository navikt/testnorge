package no.nav.registre.tp.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import no.nav.registre.tp.database.multitenancy.MultitenantDataSource;

@Slf4j
@Configuration
public class MultitenantConfiguration {

    private final List<String> databaseEnvironments = new ArrayList<String>() {{
        this.add("q2");
        this.add("q11");

    }};
    @Autowired
    private DataSourceProperties properties;

    /**
     * Defines the data source for the application
     *
     * @return
     */
    @Bean
    public DataSource dataSource() {
        Map<Object, Object> resolvedDataSources = new HashMap<>();
        for (String env : databaseEnvironments) {
            Resource resource = new ClassPathResource(String.format("/hibernate-database-%s.properties", env));
            try {
                Properties tenantProperties = PropertiesLoaderUtils.loadProperties(resource);
                DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create(this.getClass().getClassLoader());
                // Assumption: The tenant database uses the same driver class
                // as the default database that you configure.
                dataSourceBuilder
                        .driverClassName(properties.getDriverClassName())
                        .url(tenantProperties.getProperty("hibernate.connection.url"))
                        .username(tenantProperties.getProperty("hibernate.connection.username"))
                        .password(tenantProperties.getProperty("hibernate.connection.password"));

                if (properties.getType() != null) {
                    dataSourceBuilder.type(properties.getType());
                }

                resolvedDataSources.put(env, dataSourceBuilder.build());
            } catch (IOException e) {
                log.error("Unable to read database prop");
            }
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
        DataSourceBuilder dataSourceBuilder = DataSourceBuilder.create(this.getClass().getClassLoader())
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