package no.nav.registre.testnorge.libs.analysisautoconfiguration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.ApplicationProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.ApplikasjonsanalyseConsumer;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.DependencyDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Service
@RequiredArgsConstructor
public class AutoAnalyseService {
    private final ApplicationProperties properties;
    private final List<NaisServerProperties> serverProperties;
    private final ApplikasjonsanalyseConsumer applikasjonsanalyseConsumer;

    @Async
    public void analyse() {
        applikasjonsanalyseConsumer.save(ApplicationInfoDTO
                .builder()
                .cluster(properties.getCluster())
                .name(properties.getName())
                .namespace(properties.getNamespace())
                .dependencies(serverProperties.stream().map(value -> DependencyDTO
                        .builder()
                        .cluster(value.getCluster())
                        .name(value.getName())
                        .namespace(value.getNamespace())
                        .build()
                ).collect(Collectors.toSet()))
                .build()
        );
    }
}
