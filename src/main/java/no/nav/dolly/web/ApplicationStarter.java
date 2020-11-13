package no.nav.dolly.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.dependencyanalysis.provider.DependenciesController;

@EnableZuulProxy
@SpringBootApplication
@Import(DependenciesController.class)
public class ApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationStarter.class, args);
    }
}
