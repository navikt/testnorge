package no.nav.no.registere.testnorge.arbeidsforholdexportapi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
public class JDBCConfig {
    private final String username;
    private final String password;
    private final String url;

    public JDBCConfig(
            @Value("${database.aareg.username}") String username,
            @Value("${database.aareg.password}") String password,
            @Value("${database.aareg.url}") String url
    ) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("oracle.jdbc.driver.OracleDriver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        return dataSource;
    }
}
