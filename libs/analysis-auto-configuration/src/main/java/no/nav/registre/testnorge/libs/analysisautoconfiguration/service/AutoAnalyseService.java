package no.nav.registre.testnorge.libs.analysisautoconfiguration.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials.ApplikasjonsanalyseServiceProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.ApplikasjonsanalyseConsumer;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.DependencyDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Slf4j
@Service
@RequiredArgsConstructor
public class AutoAnalyseService {
    private final List<NaisServerProperties> serverProperties;
    private final ApplikasjonsanalyseConsumer applikasjonsanalyseConsumer;

    @Async
    public void analyse(String name, String namespace, String cluster) {
        log.info("Sender applikasjon informasjon...");
        applikasjonsanalyseConsumer.save(ApplicationInfoDTO
                .builder()
                .cluster(cluster)
                .name(name)
                .namespace(namespace)
                .dependencies(serverProperties.stream().filter(value -> !(value instanceof ApplikasjonsanalyseServiceProperties))
                        .map(value -> DependencyDTO.builder()
                                .cluster(value.getCluster())
                                .name(value.getName())
                                .namespace(value.getNamespace())
                                .build()
                        ).collect(Collectors.toSet()))
                .build()
        );
        log.info("Applikasjon informasjon sent.");
    }
}
