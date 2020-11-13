package no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

import no.nav.registre.testnorge.libs.autoconfigdependencyanalysis.consumer.AvhengighetsanalyseServiceConsumer;
import no.nav.registre.testnorge.libs.oauth2.service.ClientCredentialGenerateWithoutLoginAccessTokenService;

@Slf4j
@Configuration
@Import({
        AvhengighetsanalyseServiceClientCredential.class,
        AvhengighetsanalyseServiceConsumer.class,
        ClientCredentialGenerateWithoutLoginAccessTokenService.class
})
@Profile("prod")
@EnableAsync
public class DependencyAnalysisAutoConfiguration {
    private final AvhengighetsanalyseServiceConsumer avhengighetsanalyseServiceConsumer;
    private final AvhengighetsanalyseServiceClientCredential avhengighetsanalyseServiceClientCredential;
    private final String applicationName;

    public DependencyAnalysisAutoConfiguration(
            AvhengighetsanalyseServiceConsumer avhengighetsanalyseServiceConsumer,
            AvhengighetsanalyseServiceClientCredential avhengighetsanalyseServiceClientCredential,
            @Value("${application.name:${spring.application.name:#{null}}}") String applicationName
    ) {
        this.avhengighetsanalyseServiceConsumer = avhengighetsanalyseServiceConsumer;
        this.avhengighetsanalyseServiceClientCredential = avhengighetsanalyseServiceClientCredential;
        this.applicationName = applicationName;
    }

    @Async
    @PostConstruct
    public void init() {
        if (avhengighetsanalyseServiceClientCredential.getClientSecret() == null) {
            log.warn("Kan ikke registrere app fordi AVHENGIGHETSANALYSE_SERVICE_CLIENT_SECRET ikke er satt i vault.");
            return;
        }

        if (applicationName == null) {
            log.warn("Registrerer ikke avhengigheter fordi applikasjonsnavn ikke er satt.");
            return;
        }

        try {
            avhengighetsanalyseServiceConsumer.registerApplication(applicationName);
        } catch (Exception e) {
            log.error("Klarte ikke å registrere {}.", applicationName, e);
        }
    }
}
