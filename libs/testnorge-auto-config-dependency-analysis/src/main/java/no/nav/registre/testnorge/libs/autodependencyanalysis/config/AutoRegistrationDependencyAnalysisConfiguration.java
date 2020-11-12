package no.nav.registre.testnorge.libs.autodependencyanalysis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;

import javax.annotation.PostConstruct;

import no.nav.registre.testnorge.libs.autodependencyanalysis.consumer.AvhengighetsanalyseServiceConsumer;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateWithoutLoginAccessTokenService;

@Slf4j
@Configuration
@Import({
        AvhengighetsanalyseServiceClientCredential.class,
        AvhengighetsanalyseServiceConsumer.class,
        ClientCredentialGenerateWithoutLoginAccessTokenService.class
})
@Order(100)
@Profile("prod")
public class AutoRegistrationDependencyAnalysisConfiguration {
    private final AvhengighetsanalyseServiceConsumer avhengighetsanalyseServiceConsumer;
    private final String applicationName;

    public AutoRegistrationDependencyAnalysisConfiguration(
            AvhengighetsanalyseServiceConsumer avhengighetsanalyseServiceConsumer,
            @Value("${application.name:${spring.application.name:#{null}}}") String applicationName
    ) {
        this.avhengighetsanalyseServiceConsumer = avhengighetsanalyseServiceConsumer;
        this.applicationName = applicationName;
    }

    @PostConstruct
    public void init() {
        try {
            if (applicationName != null) {
                avhengighetsanalyseServiceConsumer.registerApplication(applicationName);
            } else {
                log.warn("Registerer ikke avhengigheter fordi applikasjonsnavn navn ikke er satt.");
            }
        } catch (Exception e) {
            log.error("Klarete ikke å registere {}.", applicationName, e);
        }
    }
}
