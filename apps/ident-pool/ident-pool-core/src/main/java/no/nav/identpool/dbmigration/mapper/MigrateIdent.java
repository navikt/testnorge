package no.nav.identpool.dbmigration.mapper;

import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.domain.oracle.OraIdent;
import no.nav.identpool.domain.postgres.Ident;
import no.nav.identpool.repository.oracle.OraIdentRepository;
import no.nav.identpool.repository.postgres.IdentRepository;

@Slf4j
@Service
@Order(1)
@RequiredArgsConstructor
public class MigrateIdent implements MigrationService {

    private static final int PAGE_SIZE = 100;

    private final OraIdentRepository oraIdentRepository;
    private final IdentRepository identRepository;

    @Override public void migrate() {

        int pageNo = 0;
        Page<OraIdent> identer = oraIdentRepository.findAllByOrderByIdentity(PageRequest.of(pageNo, PAGE_SIZE));
        while (!identer.getContent().isEmpty()) {
            identRepository.saveAll(
                    identer.getContent().stream()
                            .map(MigrateIdent::getIdent)
                            .collect(Collectors.toList()));
            pageNo++;
            log.info("Migrert {} antall personidentifikatorer", pageNo * PAGE_SIZE);
            identer = oraIdentRepository.findAllByOrderByIdentity(PageRequest.of(pageNo, PAGE_SIZE));
        }

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
