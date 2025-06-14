package no.nav.testnav.identpool.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.r2dbc.connection.init.CompositeDatabasePopulator;
import org.springframework.r2dbc.connection.init.ConnectionFactoryInitializer;
import org.springframework.r2dbc.connection.init.ResourceDatabasePopulator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class TestDatabaseFlywayConfiguration {

    private static final String DB_MIGRATION = "db/migration";

    @Bean
    public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) throws URISyntaxException, IOException {

        var initializer = new ConnectionFactoryInitializer();
        initializer.setConnectionFactory(connectionFactory);
        var populator = new CompositeDatabasePopulator();
        getDbFiles().forEach(file ->
                populator.addPopulators(new ResourceDatabasePopulator(new ClassPathResource(file))));
        initializer.setDatabasePopulator(populator);
        return initializer;
    }

    private static List<String> getDbFiles() throws URISyntaxException, IOException {

        var url = ClassPathResource.class.getClassLoader().getResource(DB_MIGRATION);
        return Files.list(Paths.get(url.toURI()))
                .map(path -> DB_MIGRATION + "/" + path.getFileName())
                .sorted()
                .toList();
    }
}