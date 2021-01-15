package no.nav.registre.testnorge.libs.localdevelopment;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(VaultConfig.class)
public class LocalDevelopmentConfig {
}
