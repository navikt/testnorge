package no.nav.registre.testnorge.jenkinsbatchstatusservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class JenkinsBatchStatusServiceApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(JenkinsBatchStatusServiceApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}