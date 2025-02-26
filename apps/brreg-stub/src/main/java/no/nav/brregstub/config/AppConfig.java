package no.nav.brregstub.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.brregstub.database.repository")
@Import({
        ApplicationCoreConfig.class
})
public class AppConfig {
}
