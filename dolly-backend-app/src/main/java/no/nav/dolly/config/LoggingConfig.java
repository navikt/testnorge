package no.nav.dolly.config;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptionsMethodInterceptor;
import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptionsPointcutAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggingConfig {

    @Bean
    LogExceptionsPointcutAdvisor logExceptionsPointcutAdvisor() {
        return new LogExceptionsPointcutAdvisor();
    }

    @Bean
    LogExceptionsMethodInterceptor logExceptionsMethodInterceptor() {
        Logger errorLogger = LoggerFactory.getLogger("tekniskeFeil");
        Logger functionalLogger = LoggerFactory.getLogger("funksjonelleFeil");
        return new LogExceptionsMethodInterceptor(errorLogger, functionalLogger);
    }
}
