package no.nav.registre.testnorge.libs.analysisautoconfiguration.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials.ApplikasjonsanalyseServiceFSSProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.ApplikasjonsanalyseConsumer;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.service.AutoAnalyseService;

@Slf4j
@Configuration
@Import({
        AutoAnalyseService.class,
        ApplikasjonsanalyseServiceFSSProperties.class,
        ApplikasjonsanalyseConsumer.class
})
@EnableAsync
@Profile("prod")
public class AnalysisFSSAutoConfiguration {

    public AnalysisFSSAutoConfiguration(
            AutoAnalyseService analyseService,
            @Value("${application.name:${spring.application.name}}") String name,
            @Value("${application.namespace:${spring.application.namespace:dolly}}") String namespace,
            @Value("${application.cluster:${spring.application.cluster:dev-fss}}") String cluster
    ) {
        this.analyseService = analyseService;
        this.name = name;
        this.namespace = namespace;
        this.cluster = cluster;
    }

    private final AutoAnalyseService analyseService;
    private final String name;
    private final String namespace;
    private final String cluster;

    @PostConstruct
    public void init() {
        log.info("Analyserer {}...", name);
        analyseService.analyse(name, namespace, cluster);
        log.info("Analysering feridg.");
    }
}