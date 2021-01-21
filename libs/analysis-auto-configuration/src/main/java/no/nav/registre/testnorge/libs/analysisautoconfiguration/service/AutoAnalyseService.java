package no.nav.registre.testnorge.libs.analysisautoconfiguration.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Service
public class AutoAnalyseService {
    private final ApplicationProperties properties;
    private final List<NaisServerProperties> serverProperties;

    public AutoAnalyseService(ApplicationProperties properties, List<NaisServerProperties> serverProperties) {
        this.properties = properties;
        this.serverProperties = serverProperties;
    }

    @Async
    public void analyse() {
        int i = 0;
    }
}
