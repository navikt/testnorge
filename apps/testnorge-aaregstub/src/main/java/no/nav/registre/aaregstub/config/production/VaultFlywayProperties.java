package no.nav.registre.aaregstub.config.production;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.validation.constraints.NotEmpty;

@Data
@ConfigurationProperties("app.db.flyway")
public class VaultFlywayProperties {

    @NotEmpty
    private String role;
    @NotEmpty
    private String backend;
}
