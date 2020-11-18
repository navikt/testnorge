package no.nav.dolly.dbmigrate.mapper;

import static org.apache.logging.log4j.util.Strings.isNotBlank;

import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.oracle.OraBestilling;
import no.nav.dolly.domain.jpa.oracle.OraBestillingKontroll;
import no.nav.dolly.domain.jpa.oracle.OraBestillingProgress;
import no.nav.dolly.domain.jpa.oracle.OraTransaksjonMapping;
import no.nav.dolly.domain.jpa.postgres.Bestilling;
import no.nav.dolly.domain.jpa.postgres.BestillingKontroll;
import no.nav.dolly.domain.jpa.postgres.BestillingProgress;
import no.nav.dolly.domain.jpa.postgres.Bruker;
import no.nav.dolly.domain.jpa.postgres.Testgruppe;
import no.nav.dolly.domain.jpa.postgres.TransaksjonMapping;
import no.nav.dolly.repository.oracle.OraBestillingRepository;
import no.nav.dolly.repository.postgres.BestillingKontrollRepository;
import no.nav.dolly.repository.postgres.BestillingProgressRepository;
import no.nav.dolly.repository.postgres.BestillingRepository;
import no.nav.dolly.repository.postgres.BrukerRepository;
import no.nav.dolly.repository.postgres.TestgruppeRepository;
import no.nav.dolly.repository.postgres.TransaksjonMappingRepository;

@Slf4j
@Service
@Order(4)
@RequiredArgsConstructor
public class BestillingMigrationService implements MigrationService {

    private final BestillingRepository bestillingRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingKontrollRepository bestillingKontrollRepository;
    private final OraBestillingRepository oraBestillingRepository;
    private final BrukerRepository brukerRepository;
    private final TestgruppeRepository testgruppeRepository;
    private final TransaksjonMappingRepository transaksjonMappingRepository;

    @Override
    @Transactional(propagation = Propagation.NESTED)
    public void migrate() {

        Map<String, Bruker> brukere = brukerRepository.findAllByOrderById().stream()
                .collect(Collectors.toMap(MigrationService::getBrukerId, bruker -> bruker));
        Map<String, Testgruppe> testgrupper = testgruppeRepository.findAllByOrderByNavn().stream()
                .collect(Collectors.toMap(Testgruppe::toString, gruppe -> gruppe));

        Iterable<OraBestilling> oraBestillinger = oraBestillingRepository.findAllByOrderByIdAsc();

        oraBestillinger.forEach(bestilling -> {

            Bestilling bestResultat = bestillingRepository.save(mapBestilling(bestilling, brukere, testgrupper));
            bestilling.getProgresser().forEach(progress ->
                    bestillingProgressRepository.save(mapProgress(progress, bestResultat.getId())));
            bestilling.getKontroller().forEach(kontroll ->
                    bestillingKontrollRepository.save(mapKontroll(kontroll, bestResultat.getId())));
            bestilling.getTransaksjonmapping().forEach(transaksjon ->
                    transaksjonMappingRepository.save(mapTransaksjon(transaksjon, bestResultat.getId())));
        });

        log.info("Migrert bestilling, bestillingProgress, bestillingKontroll og transaksjonMapping del I");
    }

    private static Bestilling mapBestilling(OraBestilling bestilling, Map<String, Bruker> brukere, Map<String, Testgruppe> grupper) {

        return Bestilling.builder()
                .gruppe(grupper.get(bestilling.getGruppe().toString()))
                .miljoer(bestilling.getMiljoer())
                .antallIdenter(bestilling.getAntallIdenter())
                .sistOppdatert(bestilling.getSistOppdatert())
                .ferdig(bestilling.isFerdig())
                .stoppet(bestilling.isStoppet())
                .feil(bestilling.getFeil())
                .opprettetFraId(bestilling.getOpprettetFraId())
                .tpsfKriterier(bestilling.getTpsfKriterier())
                .openamSent(bestilling.getOpenamSent())
                .opprettFraIdenter(bestilling.getOpprettFraIdenter())
                .malBestillingNavn(bestilling.getMalBestillingNavn())
                .ident(bestilling.getIdent())
                .tpsImport(bestilling.getTpsImport())
                .kildeMiljoe(bestilling.getKildeMiljoe())
                .bestKriterier(bestilling.getBestKriterier())
                .bruker(brukere.get(MigrationService.getBrukerId(bestilling.getBruker())))
                .build();
    }

    private static BestillingProgress mapProgress(OraBestillingProgress progress, Long bestillingId) {

        return BestillingProgress.builder()
                .bestillingId(bestillingId)
                .tpsfSuccessEnv(removeNullChar(progress.getTpsfSuccessEnv()))
                .sigrunstubStatus(progress.getSigrunstubStatus())
                .feil(removeNullChar(progress.getFeil()))
                .ident(progress.getIdent())
                .krrstubStatus(progress.getKrrstubStatus())
                .aaregStatus(progress.getAaregStatus())
                .arenaforvalterStatus(progress.getArenaforvalterStatus())
                .pdlforvalterStatus(progress.getPdlforvalterStatus())
                .instdataStatus(progress.getInstdataStatus())
                .udistubStatus(progress.getUdistubStatus())
                .inntektstubStatus(progress.getInntektstubStatus())
                .pensjonforvalterStatus(progress.getPensjonforvalterStatus())
                .inntektsmeldingStatus(progress.getInntektsmeldingStatus())
                .brregstubStatus(progress.getBrregstubStatus())
                .dokarkivStatus(progress.getDokarkivStatus())
                .sykemeldingStatus(progress.getSykemeldingStatus())
                .tpsImportStatus(progress.getTpsImportStatus())
                .skjermingsregisterStatus(progress.getSkjermingsregisterStatus())
                .build();
    }

    private static BestillingKontroll mapKontroll(OraBestillingKontroll kontroll, Long id) {

        return BestillingKontroll.builder()
                .bestillingId(id)
                .stoppet(kontroll.isStoppet())
                .build();
    }

    private static TransaksjonMapping mapTransaksjon(OraTransaksjonMapping transMap, Long bestillingId) {

        return TransaksjonMapping.builder()
                .bestillingId(bestillingId)
                .datoEndret(transMap.getDatoEndret())
                .ident(transMap.getIdent())
                .miljoe(transMap.getMiljoe())
                .system(transMap.getSystem())
                .transaksjonId(transMap.getTransaksjonId())
                .build();
    }

    private static String removeNullChar(String value) {

        return isNotBlank(value) ? value.replace("\u0000", "") : null;
    }
}
