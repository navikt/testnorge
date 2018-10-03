package no.nav.registre.orkestratoren;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({InternalController.class, JobController.class})
public class AppConfig {

}
