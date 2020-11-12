package no.nav.registre.testnorge.libs.autodependencyanalysis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import no.nav.registre.testnorge.libs.autodependencyanalysis.consumer.AvhengighetsanalyseServiceConsumer;
import no.nav.registre.testnorge.libs.autodependencyanalysis.service.ApplicationRegistrationService;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateWithoutLoginAccessTokenService;

@Slf4j
@Configuration
@Import({
        AvhengighetsanalyseServiceClientCredential.class,
        AvhengighetsanalyseServiceConsumer.class,
        ClientCredentialGenerateWithoutLoginAccessTokenService.class,
        ApplicationRegistrationService.class
})
public class AutoRegistrationDependencyAnalysisConfiguration {
}
