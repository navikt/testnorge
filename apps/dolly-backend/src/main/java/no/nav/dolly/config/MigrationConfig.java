package no.nav.dolly.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.service.MigrateDokumentService;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import javax.annotation.PostConstruct;

@Slf4j
@Configuration
@Profile("!test")
@RequiredArgsConstructor
public class MigrationConfig {

    private final MigrateDokumentService migrateDokumentService;

    @PostConstruct
    public void migrateDokumenter() {

        var time = System.currentTimeMillis();
        log.info("Starter migrering av dokumenter.");

        migrateDokumentService.migrateDokumenter();

        log.info("Migrering av dokumenter ferdig, medgått tid: {} sekunder", (System.currentTimeMillis() - time) / 1000);
    }
}
