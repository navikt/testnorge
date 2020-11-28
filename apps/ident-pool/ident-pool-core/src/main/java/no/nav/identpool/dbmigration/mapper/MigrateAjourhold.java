package no.nav.identpool.dbmigration.mapper;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.oracle.OraAjourhold;
import no.nav.identpool.domain.postgres.Ajourhold;
import no.nav.identpool.repository.oracle.OraAjourholdRepository;
import no.nav.identpool.repository.postgres.AjourholdRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrateAjourhold implements MigrationService {

    private final OraAjourholdRepository oraAjourholdRepository;
    private final AjourholdRepository ajourholdRepository;

    @Override public void migrate() {

        Iterable<OraAjourhold> ajourhold = oraAjourholdRepository.findAll();
        ajourhold.forEach(oraAjourhold -> ajourholdRepository.save(getAjourhold(oraAjourhold)));

        log.info("Migrert Ajourhold tabell");
    }

    private static Ajourhold getAjourhold(OraAjourhold ajourhold) {

        return Ajourhold.builder()
                .identity(ajourhold.getIdentity())
                .status(ajourhold.getStatus())
                .feilmelding(ajourhold.getFeilmelding())
                .sistOppdatert(ajourhold.getSistOppdatert())
                .build();
    }
}
