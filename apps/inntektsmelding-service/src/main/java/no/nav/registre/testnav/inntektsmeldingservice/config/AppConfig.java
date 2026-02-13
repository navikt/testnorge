package no.nav.registre.testnav.inntektsmeldingservice.config;

import no.nav.testnav.libs.reactivecore.config.CoreConfig;
import no.nav.testnav.libs.reactivesecurity.config.SecureOAuth2ServerToServerConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@Configuration
@Import({CoreConfig.class,
        SecureOAuth2ServerToServerConfiguration.class})
public class AppConfig {

}
