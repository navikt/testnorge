package no.nav.testnav.examples.reactiverestexample.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}