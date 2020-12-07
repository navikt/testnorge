package no.nav.identpool.dbmigration.mapper;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.oracle.OraWhitelist;
import no.nav.identpool.domain.postgres.Whitelist;
import no.nav.identpool.repository.oracle.OraWhitelistRepository;
import no.nav.identpool.repository.postgres.WhitelistRepository;

@Slf4j
@Order(1)
@Service
@RequiredArgsConstructor
public class MigrateWhitelist implements MigrationService {

    private final OraWhitelistRepository oraWhitelistRepository;
    private final WhitelistRepository whitelistRepository;

    @Override public void migrate() {

        Iterable<OraWhitelist> whitelist = oraWhitelistRepository.findAll();
        whitelist.forEach(oraAjourhold -> whitelistRepository.save(getWhitelist(oraAjourhold)));

        log.info("Migrert Whitelist tabell");
    }

    private static Whitelist getWhitelist(OraWhitelist whitelist) {

        return Whitelist.builder()
                .id(whitelist.getId())
                .fnr(whitelist.getFnr())
                .build();
    }
}
