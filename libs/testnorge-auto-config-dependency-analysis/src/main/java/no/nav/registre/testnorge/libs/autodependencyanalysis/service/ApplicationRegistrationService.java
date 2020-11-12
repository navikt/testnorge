package no.nav.registre.testnorge.libs.autodependencyanalysis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import no.nav.registre.testnorge.libs.autodependencyanalysis.consumer.AvhengighetsanalyseServiceConsumer;

@Slf4j
@Service
public class ApplicationRegistrationService {
    private final AvhengighetsanalyseServiceConsumer avhengighetsanalyseServiceConsumer;
    private final String applicationName;

    public ApplicationRegistrationService(
            AvhengighetsanalyseServiceConsumer avhengighetsanalyseServiceConsumer,
            @Value("${application.name:${spring.application.name:#{null}}}") String applicationName
    ) {
        this.avhengighetsanalyseServiceConsumer = avhengighetsanalyseServiceConsumer;
        this.applicationName = applicationName;
    }


    public void register() {
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
