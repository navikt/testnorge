package no.nav.dolly.budpro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;

@TestConfiguration(proxyBeanMethods = false)
public class TestBudproServiceApplication {

    public static void main(String[] args) {
        SpringApplication.from(BudproServiceApplication::main).with(TestBudproServiceApplication.class).run(args);
    }

}
