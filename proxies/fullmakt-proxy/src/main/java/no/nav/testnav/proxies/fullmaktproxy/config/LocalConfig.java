package no.nav.testnav.proxies.fullmaktproxy.config;

import no.nav.testnav.libs.vault.AbstractLocalVaultConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
@Import({
})
public class LocalConfig extends AbstractLocalVaultConfiguration {
} 