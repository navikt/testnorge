package no.nav.identpool.dbmigration;

import java.util.List;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.dbmigration.mapper.MigrationService;
import no.nav.identpool.repository.postgres.WhitelistRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class MigrateDatabase implements InitializingBean {

    private final List<MigrationService> migrationServices;
    private final WhitelistRepository whitelistRepository;

    @Override
    public void afterPropertiesSet() {

        if (!whitelistRepository.findAllBy().isEmpty()) {
            log.info("Database er migrert");

        } else {
            log.info("Migrering av database starter");

            migrationServices.forEach(MigrationService::migrate);

            log.info(("Migrering ferdig"));
        }
    }
}
