package no.nav.registre.testnorge.identservice.config;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(value = {
        ApplicationCoreConfig.class
})
public class AppConfig {

}
