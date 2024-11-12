package no.nav.testnav.altinn3tilgangservice.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import static lombok.AccessLevel.PACKAGE;


@Configuration
@ConfigurationProperties
@NoArgsConstructor(access = PACKAGE)
@Getter
@Setter(PACKAGE)
public class MaskinportenConfig {

    private String maskinportenClientId;
    private String maskinportenClientJwk;
    private String maskinportenScopes;
    private String maskinportenWellKnownUrl;
}