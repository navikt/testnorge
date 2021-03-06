package no.nav.registre.inntekt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

import no.nav.registre.testnorge.libs.localdevelopment.LocalDevelopmentConfig;

@Configuration
@Profile("local")
@Import(LocalDevelopmentConfig.class)
public class DevConfig {

}
