package no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials;

import org.springframework.context.annotation.Configuration;


@Configuration
public class ApplikasjonsanalyseServiceGCPProperties extends ApplikasjonsanalyseServiceProperties {

    public ApplikasjonsanalyseServiceGCPProperties() {
        setUrl("http://applikasjonsanalyse-service.dolly.svc.cluster.local");
        setCluster("dev-gcp");
        setNamespace("dolly");
        setName("applikasjonanalyse-service");
    }
}
