package no.nav.registre.testnorge.libs.core.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
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
@ConditionalOnProperty(prefix = "testnorge.analyse", name="enabled", havingValue = "true")
public class AnalysisFSSAutoConfiguration {

    private final AutoAnalyseService analyseService;
    private final String name;
    private final String namespace;
    private final String cluster;

    public AnalysisFSSAutoConfiguration(
            AutoAnalyseService analyseService,
            @Value("${application.name:${spring.application.name:unknown}}") String name,
            @Value("${analyse.namespace:dolly}") String namespace,
            @Value("${analyse.cluster:dev-fss}") String cluster
    ) {
        this.name = name;
        this.namespace = namespace;
        this.cluster = cluster;
        this.analyseService = analyseService;
    }

    @PostConstruct
    public void init() {
        log.info("Analyserer {}...", name);
        analyseService.analyse(name, namespace, cluster);
        log.info("Analysering ferdig.");
    }

}