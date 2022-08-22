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
public class TokendingsMockApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(TokendingsMockApplicationStarter.class, args);
    }
}
