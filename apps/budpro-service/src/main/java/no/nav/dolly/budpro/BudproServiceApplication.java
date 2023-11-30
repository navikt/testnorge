package no.nav.dolly.budpro;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class BudproServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(BudproServiceApplication.class, args);
    }
}
