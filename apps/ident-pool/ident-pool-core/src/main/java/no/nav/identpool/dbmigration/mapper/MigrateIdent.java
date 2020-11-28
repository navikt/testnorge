package no.nav.identpool.dbmigration.mapper;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
@Order(3)
@Service
@RequiredArgsConstructor
public class MigrateIdent implements MigrationService {

    private static final int PAGE_SIZE = 100;

    private final OraIdentRepository oraIdentRepository;
    private final IdentRepository identRepository;

    @Override public void migrate() {

        Page<OraIdent> identer = oraIdentRepository.findAllByOrderByIdentity(PageRequest.of(0, PAGE_SIZE));
        IntStream.range(0, identer.getTotalPages())
                .boxed()
                .parallel()
                .map(pageNo -> {
                    identRepository.saveAll(
                            oraIdentRepository.findAllByOrderByIdentity(PageRequest.of(pageNo, PAGE_SIZE))
                                    .getContent().stream()
                                    .map(MigrateIdent::getIdent)
                                    .collect(Collectors.toList()));
                    log.info("Migrert page {} of total {} personidentifikatorer", pageNo, identer.getTotalPages());
                    return pageNo;
                })
                .collect(Collectors.toList());

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
