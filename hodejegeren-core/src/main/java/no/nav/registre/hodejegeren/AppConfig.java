package no.nav.registre.hodejegeren;

import no.nav.registre.hodejegeren.provider.rs.InternalController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({ InternalController.class, JobController.class })
public class AppConfig {
}