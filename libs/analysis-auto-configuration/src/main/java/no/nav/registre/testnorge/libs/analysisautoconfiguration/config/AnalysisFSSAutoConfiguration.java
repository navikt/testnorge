package no.nav.registre.testnorge.libs.analysisautoconfiguration.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.PostConstruct;

import no.nav.registre.testnorge.libs.analysisautoconfiguration.config.credentials.ApplikasjonsanalyseServiceFSSProperties;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.consumer.ApplikasjonsanalyseConsumer;
import no.nav.registre.testnorge.libs.analysisautoconfiguration.service.AutoAnalyseService;

@Slf4j
@Configuration
@Import({
        ApplicationProperties.class,
        AutoAnalyseService.class,
        ApplikasjonsanalyseServiceFSSProperties.class,
        ApplikasjonsanalyseConsumer.class
})
@EnableAsync
@RequiredArgsConstructor
@Profile("prod")
public class AnalysisFSSAutoConfiguration {

    private final ApplicationProperties properties;
    private final AutoAnalyseService analyseService;

    @Async
    @PostConstruct
    public void init() {
        log.info("Analyserer {}...", properties.getName());
        analyseService.analyse();
        log.info("Analysering feridg.");
    }

}