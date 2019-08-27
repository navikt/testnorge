package no.nav.registre.sdForvalter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Slf4j
@SpringBootApplication(scanBasePackages = "no.nav.registre")
@EnableJpaRepositories(basePackages = "no.nav.registre")
@PropertySource("classpath:application-local.properties")
public class LocalApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(LocalApplicationStarter.class, args);
    }
}

