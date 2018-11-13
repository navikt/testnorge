package no.nav.registre.hodejegeren.config.logging;

import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptionsMethodInterceptor;
import no.nav.freg.spring.boot.starters.log.exceptions.LogExceptionsPointcutAdvisor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogExceptionsConfig {
    @Bean
    public LogExceptionsPointcutAdvisor logExceptionsPointcutAdvisor() {
        return new LogExceptionsPointcutAdvisor();
    }

    @Bean
    public LogExceptionsMethodInterceptor logExceptionsMethodInterceptor() {
        Logger exceptionLogger = LoggerFactory.getLogger("ExceptionLogger");
        Logger functionalLogger = LoggerFactory.getLogger("FunctionalLogger");
        return new LogExceptionsMethodInterceptor(exceptionLogger, functionalLogger);
    }
}