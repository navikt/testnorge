package no.nav.registre.testnav.inntektsmeldingservice;

import no.nav.dolly.libs.nais.NaisEnvironmentApplicationContextInitializer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class InntektsmeldingApplicationStarter {
    public static void main(String[] args) {
        new SpringApplicationBuilder(InntektsmeldingApplicationStarter.class)
                .initializers(new NaisEnvironmentApplicationContextInitializer())
                .run(args);
    }
}