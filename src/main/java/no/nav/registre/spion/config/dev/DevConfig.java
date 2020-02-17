package no.nav.registre.spion.config.dev;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Slf4j
@Profile("!prod")
@Configuration
public class DevConfig {

}