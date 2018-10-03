package no.nav.registre.orkestratoren;

import no.nav.registre.orkestratoren.rs.InternalController;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({InternalController.class, JobController.class})
public class AppConfig {

}
