package no.nav.organisasjonforvalter;

import no.nav.registre.testnorge.libs.core.config.ApplicationCoreConfig;
import no.nav.registre.testnorge.libs.oauth2.config.InsecureOAuth2ServerToServerConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({
        ApplicationCoreConfig.class,
        InsecureOAuth2ServerToServerConfiguration.class
})
public class AppConfig {

}
