package no.nav.registre.sdforvalter.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.client.RestTemplate;

import javax.servlet.Filter;

import no.nav.registere.testnorge.core.ApplicationCoreConfig;
import no.nav.registre.testnorge.common.filter.TransactionFilter;

@Configuration
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "no.nav.registre.sdforvalter.database.repository")
@Import(ApplicationCoreConfig.class)
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public FilterRegistrationBean<Filter> transactionFilterRegistration() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.setFilter(transactionFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }

    @Bean
    public Filter transactionFilter() {
        return new TransactionFilter();
    }
}
