package no.nav.testnav.inntektsmeldinggeneratorservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@EnableWebSecurity
@SpringBootApplication
public class InntektsmeldingGeneratorApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(InntektsmeldingGeneratorApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}
