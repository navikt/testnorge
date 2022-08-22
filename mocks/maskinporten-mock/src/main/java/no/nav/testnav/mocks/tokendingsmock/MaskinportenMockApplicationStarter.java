package no.nav.testnav.mocks.tokendingsmock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.reactive.config.EnableWebFlux;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;

@Import({
        CoreConfig.class,
})
@EnableWebFlux
@SpringBootApplication
public class MaskinportenMockApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(MaskinportenMockApplicationStarter.class, args);
    }
}
