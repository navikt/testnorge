package no.nav.registere.testnorge.core.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import javax.servlet.Filter;

import no.nav.registere.testnorge.core.provider.InternalController;
import no.nav.registre.testnorge.common.filter.TransactionFilter;
import no.nav.registre.testnorge.dependencyanalysis.provider.DependenciesController;

@Configuration
@Import({InternalController.class, DependenciesController.class, ApplicationProperties.class})
public class ApplicationCoreConfig {

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