package no.nav.registre.testnorge.tilbakemeldingapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autodependencyanalysis.config.AutoRegistrationDependencyAnalysisConfiguration;

@SpringBootApplication
@Import(AutoRegistrationDependencyAnalysisConfiguration.class)
public class TilbakemeldingApiApplicationStarter {
    public static void main(String[] args) {
        SpringApplication.run(TilbakemeldingApiApplicationStarter.class, args);
    }
}
