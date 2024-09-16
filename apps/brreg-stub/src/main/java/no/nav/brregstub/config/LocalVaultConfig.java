package no.nav.brregstub.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.vault.authentication.ClientAuthentication;
import org.springframework.vault.authentication.TokenAuthentication;
import org.springframework.vault.client.VaultEndpoint;
import org.springframework.vault.config.AbstractVaultConfiguration;

@Configuration
@Profile("local")
public class LocalVaultConfig extends AbstractVaultConfiguration {

    private static final String TOKEN = "spring.cloud.vault.token";

    @Override
    public VaultEndpoint vaultEndpoint() {
        return VaultEndpoint.create("vault.adeo.no", 443);
    }

    @Override
    public ClientAuthentication clientAuthentication() {
        if (System.getenv().containsKey("VAULT_TOKEN")) {
            System.setProperty(TOKEN, System.getenv("VAULT_TOKEN"));
        }
        var token = System.getProperty(TOKEN);
        if (StringUtils.isBlank(token)) {
            throw new IllegalArgumentException("Påkrevet property '%s' er ikke satt.".formatted(TOKEN));
        }
        return new TokenAuthentication(System.getProperty(TOKEN));
    }
} 