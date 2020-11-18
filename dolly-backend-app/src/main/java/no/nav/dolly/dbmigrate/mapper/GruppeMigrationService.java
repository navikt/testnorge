package no.nav.dolly.dbmigrate.mapper;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.oracle.OraTestgruppe;
import no.nav.dolly.domain.jpa.oracle.OraTestident;
import no.nav.dolly.domain.jpa.postgres.Bruker;
import no.nav.dolly.domain.jpa.postgres.Testgruppe;
import no.nav.dolly.domain.jpa.postgres.Testident;
import no.nav.dolly.repository.oracle.OraTestgruppeRepository;
import no.nav.dolly.repository.postgres.BrukerRepository;
import no.nav.dolly.repository.postgres.IdentRepository;
import no.nav.dolly.repository.postgres.TestgruppeRepository;

@Slf4j
@Service
@Order(2)
@RequiredArgsConstructor
public class GruppeMigrationService implements MigrationService {

    private final TestgruppeRepository testgruppeRepository;
    private final OraTestgruppeRepository oraTestgruppeRepository;
    private final BrukerRepository brukerRepository;
    private final IdentRepository identRepository;

    @Override
    @Transactional
    public void migrate() {

        Map<String, Bruker> brukere = brukerRepository.findAllByOrderById().stream()
                .collect(Collectors.toMap(MigrationService::getBrukerId, bruker -> bruker));
        Iterable<OraTestgruppe> oraTestgruppeInput = oraTestgruppeRepository.findAll(Sort.by("id"));

        oraTestgruppeInput.forEach(testgruppe -> {
            Testgruppe gruppe = testgruppeRepository.save(mapTestgruppe(testgruppe, brukere));
            testgruppe.getTestidenter().forEach(testIdent ->
                    identRepository.save(mapTestident(testIdent, gruppe)));
        });
        log.info("Migrert gruppe og testIdent");
    }

    private static Testgruppe mapTestgruppe(OraTestgruppe testgruppe, Map<String, Bruker> brukere) {

        return Testgruppe.builder()
                .navn(testgruppe.getNavn())
                .hensikt(testgruppe.getHensikt())
                .datoEndret(testgruppe.getDatoEndret())
                .opprettetAv(brukere.get(MigrationService.getBrukerId(testgruppe.getOpprettetAv())))
                .sistEndretAv(brukere.get(MigrationService.getBrukerId(testgruppe.getSistEndretAv())))
                .erLaast(testgruppe.getErLaast())
                .laastBeskrivelse(testgruppe.getLaastBeskrivelse())
                .build();
    }

    private static Testident mapTestident(OraTestident testident, Testgruppe testgruppe) {

        return Testident.builder()
                .ident(testident.getIdent())
                .testgruppe(testgruppe)
                .beskrivelse(testident.getBeskrivelse())
                .iBruk(testident.getIBruk())
                .build();
    }
}
