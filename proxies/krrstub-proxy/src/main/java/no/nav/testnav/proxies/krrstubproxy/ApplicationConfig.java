package no.nav.testnav.proxies.krrstubproxy;

import no.nav.testnav.libs.servletsecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(SecureOAuth2ServerToServerConfiguration.class)
public class ApplicationConfig {
}
