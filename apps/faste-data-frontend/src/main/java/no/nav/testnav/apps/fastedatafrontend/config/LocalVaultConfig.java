package no.nav.testnav.apps.fastedatafrontend.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("dev")
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}