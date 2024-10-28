package no.nav.testnav.proxies.yrkesskadeproxy.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Profile("dev")
@Configuration
public class LocalVaultConfig extends AbstractLocalVaultConfiguration {
}