package no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials;

import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplikasjonsanalyseServiceFSSProperties extends ApplikasjonsanalyseServiceProperties {

    public ApplikasjonsanalyseServiceFSSProperties() {
        setUrl("https://applikasjonsanalyse-service.dev.intern.nav.no");
        setCluster("dev-gcp");
        setNamespace("dolly");
        setName("applikasjonanalyse-service");
    }
}
