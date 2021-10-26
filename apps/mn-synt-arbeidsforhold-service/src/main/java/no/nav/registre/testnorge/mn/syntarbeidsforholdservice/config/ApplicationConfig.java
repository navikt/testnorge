package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;

import no.nav.testnav.libs.database.config.FlywayConfiguration;
import no.nav.testnav.libs.database.config.VaultHikariConfiguration;
import no.nav.testnav.libs.servletcore.config.ApplicationCoreConfig;
import no.nav.testnav.libs.servletsecurity.config.InsecureJwtServerToServerConfiguration;

@Configuration
@Import({
        ApplicationCoreConfig.class,
        FlywayConfiguration.class,
        VaultHikariConfiguration.class,
        InsecureJwtServerToServerConfiguration.class
})
@EnableJpaAuditing
public class ApplicationConfig {

    @Bean
    public TaskExecutor executor() {
        final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setMaxPoolSize(50);
        executor.setCorePoolSize(40);
        executor.setThreadNamePrefix("thread-pool-with-security-context-");
        executor.initialize();
        return new DelegatingSecurityContextAsyncTaskExecutor(executor);
    }

}