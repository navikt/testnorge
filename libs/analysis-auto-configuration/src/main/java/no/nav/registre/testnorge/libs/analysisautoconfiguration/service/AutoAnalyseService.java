package no.nav.registre.testnorge.libs.analysisautoconfiguration.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.analysis.DependencyAnalysis;
import no.nav.registre.testnorge.libs.analysis.domain.Dependency;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials.ApplikasjonsanalyseServiceProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.ApplikasjonsanalyseConsumer;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.ApplicationInfoDTO;
import no.nav.registre.testnorge.libs.dto.applikasjonsanalyseservice.v1.DependencyDTO;
import no.nav.registre.testnorge.libs.oauth2.config.NaisServerProperties;

@Slf4j
@Service
public class AutoAnalyseService {
    private final List<NaisServerProperties> serverProperties;
    private final ApplikasjonsanalyseConsumer applikasjonsanalyseConsumer;
    private final DependencyAnalysis dependencyAnalysis;

    public AutoAnalyseService(
            List<NaisServerProperties> serverProperties,
            ApplikasjonsanalyseConsumer applikasjonsanalyseConsumer,
            @Value("${testnorge.analyse.basePackage:no.nav.registere}") String basePackage
    ) {
        this.dependencyAnalysis = new DependencyAnalysis(basePackage);
        this.serverProperties = serverProperties;
        this.applikasjonsanalyseConsumer = applikasjonsanalyseConsumer;
    }

    @Async
    public void analyse(String name, String namespace, String cluster) {
        Set<Dependency> analyze = dependencyAnalysis.analyze();

        log.info("Sender applikasjon informasjon...");
        Set<DependencyDTO> dependencies = serverProperties.stream().filter(value -> !(value instanceof ApplikasjonsanalyseServiceProperties))
                .map(value -> DependencyDTO.builder()
                        .cluster(value.getCluster())
                        .name(value.getName())
                        .namespace(value.getNamespace())
                        .build()
                ).collect(Collectors.toSet());

        dependencies.addAll(analyze
                .stream()
                .map(value -> DependencyDTO
                        .builder()
                        .cluster(value.getCluster())
                        .namespace(value.getNamespace())
                        .name(value.getName())
                        .build()
                )
                .collect(Collectors.toSet())
        );

        applikasjonsanalyseConsumer.save(ApplicationInfoDTO
                .builder()
                .cluster(cluster)
                .name(name)
                .namespace(namespace)
                .dependencies(dependencies)
                .build()
        );
        log.info("Applikasjon informasjon sent.");
    }
}
