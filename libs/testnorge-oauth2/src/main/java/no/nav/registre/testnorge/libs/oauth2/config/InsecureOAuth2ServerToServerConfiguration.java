package no.nav.registre.testnorge.libs.oauth2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateAccessTokenService;
import no.nav.registre.testnorge.libs.oauth2.service.InsecureAuthenticationTokenResolver;
import no.nav.registre.testnorge.libs.oauth2.service.OnBehalfOfGenerateAccessTokenService;


/**
 * Denne skal kun brukes for apper som er utenfor namespace dolly.
 * <p>
 * Denne skal aldri deployes til GCP
 * <p>
 * TODO Fjern når alle apper er over på namespace dolly
 */
@Configuration
@Import({
        InsecureAuthenticationTokenResolver.class,
        ClientCredentialGenerateAccessTokenService.class
})
public class InsecureOAuth2ServerToServerConfiguration {
}