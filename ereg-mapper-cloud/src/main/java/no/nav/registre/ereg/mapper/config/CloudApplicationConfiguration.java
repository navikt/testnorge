package no.nav.registre.ereg.mapper.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;

import no.nav.registre.testnorge.common.filter.TransactionFilter;

@Configuration
public class CloudApplicationConfiguration {

    @Bean
    public FilterRegistrationBean transactionFilterRegistration() {
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