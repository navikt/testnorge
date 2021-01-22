package no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials;

import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplikasjonsanalyseServiceFSSProperties extends ApplikasjonsanalyseServiceProperties {

    public ApplikasjonsanalyseServiceFSSProperties() {
        setClientId("4c17d59e-ba09-4923-a917-47efebf82cc0");
        setUrl("https://applikasjonsanalyse-service.dev.intern.nav.no");
        setCluster("dev-gcp");
        setNamespace("dolly");
        setName("applikasjonanalyse-service");
    }
}
