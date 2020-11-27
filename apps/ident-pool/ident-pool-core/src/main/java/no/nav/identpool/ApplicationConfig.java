package no.nav.identpool;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import no.nav.identpool.config.RetryConfig;
import no.nav.identpool.config.SecurityConfig;

@SpringBootApplication
@EnableConfigurationProperties
@Import({ SecurityConfig.class, RetryConfig.class})
public class ApplicationConfig {

}