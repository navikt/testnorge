package no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;


@Configuration
public class ApplikasjonsanalyseServiceGCPProperties extends ApplikasjonsanalyseServiceProperties {

    public ApplikasjonsanalyseServiceGCPProperties() {
        setClientId("4c17d59e-ba09-4923-a917-47efebf82cc0");
        setUrl("http://applikasjonsanalyse-service.dolly.svc.cluster.local");
        setCluster("dev-gcp");
        setNamespace("dolly");
        setName("applikasjonanalyse-service");
    }
}
