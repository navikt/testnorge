package no.nav.adresse.service.config;

import no.nav.registre.testnorge.libs.localdevelopment.LocalDevelopmentConfig;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@Import(LocalDevelopmentConfig.class)
public class LocalConfig {
}