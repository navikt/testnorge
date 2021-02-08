package no.nav.udistub;

import no.nav.registre.testnorge.libs.config.LoggingWebConfig;
import no.nav.registre.testnorge.libs.core.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.core.provider.InternalController;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({InternalController.class,
        ApplicationProperties.class,
        LoggingWebConfig.class})
public class AppConfig {

}
