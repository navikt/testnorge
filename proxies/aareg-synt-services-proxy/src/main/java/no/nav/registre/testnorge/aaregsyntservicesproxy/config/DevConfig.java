package no.nav.registre.testnorge.aaregsyntservicesproxy.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.localdevelopment.LocalDevelopmentConfig;

@Configuration
@Profile("dev")
@Import(LocalDevelopmentConfig.class)
public class DevConfig {
}
