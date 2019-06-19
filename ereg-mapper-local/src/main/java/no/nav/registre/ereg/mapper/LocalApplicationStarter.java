package no.nav.registre.ereg.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication(scanBasePackages = "no.nav.registre")
public class LocalApplicationStarter {

    public static void main(String[] args) {
        SpringApplication.run(LocalApplicationStarter.class, args);
    }

}
