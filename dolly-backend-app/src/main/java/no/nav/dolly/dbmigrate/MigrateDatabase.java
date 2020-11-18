package no.nav.dolly.dbmigrate;

import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.dbmigrate.mapper.MigrationService;
import no.nav.dolly.repository.postgres.BrukerRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrateDatabase implements InitializingBean {

    private final List<MigrationService> migrationServices;
    private final BrukerRepository brukerRepository;

    @Override
    public void afterPropertiesSet() {
        if (!brukerRepository.findAllByOrderById().isEmpty()) {
            log.info("Database er migrert");

        } else {
            log.info("Migrering av database starter");

            migrationServices.forEach(MigrationService::migrate);

            log.info(("Migrering ferdig"));
        }
    }
}
