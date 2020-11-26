package no.nav.identpool.dbmigration.mapper;

import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.dbmigration.MigrationService;
import no.nav.identpool.domain.oracle.OraIdent;
import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.repository.oracle.OraIdentRepository;
import no.nav.identpool.repository.postgres.IdentRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class MigrateIdent implements MigrationService {

    private final OraIdentRepository oraIdentRepository;
    private final IdentRepository identRepository;

    @Override public void migrate() {

        AtomicInteger idx = new AtomicInteger();
        Iterable<OraIdent> identer = oraIdentRepository.findAll();
        identer.forEach(oraIdent -> {
            identRepository.save(getIdent(oraIdent));
            if (idx.incrementAndGet() % 100000 == 0) {
                log.info("Migrert {} antall personidentifikatorer", idx.get());
            }
        });

        log.info("Migrert Personidentifikator tabell");
    }

    private static Ident getIdent(OraIdent ident) {

        return Ident.builder()
                .identity(ident.getIdentity())
                .identtype(ident.getIdenttype())
                .finnesHosSkatt(ident.isFinnesHosSkatt())
                .foedselsdato(ident.getFoedselsdato())
                .kjoenn(ident.getKjoenn())
                .personidentifikator(ident.getPersonidentifikator())
                .rekvireringsstatus(ident.getRekvireringsstatus())
                .rekvirertAv(ident.getRekvirertAv())
                .build();
    }
}
