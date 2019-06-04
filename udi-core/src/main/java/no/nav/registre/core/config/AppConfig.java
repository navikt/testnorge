package no.nav.registre.core.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = {"no.nav.registre.core.database.model", "no.nav.registre.core.database.repository"})
@EntityScan(basePackages = "no.nav.registre.core.database.model")
@ComponentScan("no.nav.registre.core")
public class AppConfig {

}
