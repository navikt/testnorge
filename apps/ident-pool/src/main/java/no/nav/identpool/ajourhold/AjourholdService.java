package no.nav.identpool.ajourhold;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.consumers.TpsMessagingConsumer;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.dto.TpsStatusDTO;
import no.nav.identpool.providers.v1.support.HentIdenterRequest;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.service.IdentGeneratorService;
import no.nav.identpool.util.IdentGeneratorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;

@Slf4j
@Service
@RequiredArgsConstructor
public class AjourholdService {

    private static final String NAV_SYNTETISKE = "NAV-syntetiske";
    private static final String VANLIGE = "vanlige";

    private static final int MAX_SIZE_TPS_QUEUE = 80;
    private static final int MIN_ANTALL_IDENTER_PER_DAG = 2;
    private static final int MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR = 10;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final TpsMessagingConsumer tpsMessagingConsumer;

    public void checkCriticalAndGenerate() {

        int minYearMinus = 110;
        LocalDate minDate = LocalDate.now().minusYears(minYearMinus).with(firstDayOfYear());
        while (minDate.isBefore(LocalDate.now().plusYears(1))) {
            checkAndGenerateForDate(minDate.getYear(), Identtype.FNR, false);
            checkAndGenerateForDate(minDate.getYear(), Identtype.FNR, true);
            checkAndGenerateForDate(minDate.getYear(), Identtype.DNR, false);
            checkAndGenerateForDate(minDate.getYear(), Identtype.DNR, true);
            checkAndGenerateForDate(minDate.getYear(), Identtype.BOST, false);
            checkAndGenerateForDate(minDate.getYear(), Identtype.BOST, true);
            minDate = minDate.plusYears(1);
        }
    }

    void checkAndGenerateForDate(
            int year,
            Identtype type,
            boolean syntetiskIdent) {

        int numberOfMissingIdents;
        var maxRuns = 3;
        var runs = 0;

        do {
            numberOfMissingIdents = getNumberOfMissingIdents(year, type, syntetiskIdent);
            if (numberOfMissingIdents > 0) {
                numberOfMissingIdents -= generateForYear(year, type, numberOfMissingIdents, syntetiskIdent);
            } else {
                log.info("Ajourhold: år {} {} {} identer har tilstrekkelig antall",
                        year, type, syntetiskIdent ? NAV_SYNTETISKE : VANLIGE);
                break;
            }
            runs++;

        } while (numberOfMissingIdents > 0 && runs < maxRuns);
    }

    private int getNumberOfMissingIdents(
            int year,
            Identtype type,
            Boolean syntetiskIdent) {

        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year);
        antallPerDag = adjustForYear(year, antallPerDag);
        int days = (year == LocalDate.now().getYear() ? 365 - (365 - LocalDate.now().getDayOfYear()) : 365);
        long count = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatusAndSyntetisk(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                type,
                LEDIG,
                syntetiskIdent);
        return Math.toIntExact((antallPerDag * days) - count);
    }

    int generateForYear(
            int year,
            Identtype type,
            int numberOfIdents,
            boolean syntetiskIdent) {

        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year + 1) * 2;
        antallPerDag = adjustForYear(year, antallPerDag);

        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year + 1, 1, 1);
        if (lastDate.isAfter(LocalDate.now())) {
            lastDate = LocalDate.of(year, LocalDate.now().getMonth(), LocalDate.now().getDayOfMonth());
        }
        if (lastDate.isEqual(firstDate)) {
            lastDate = lastDate.plusDays(1);
        }

        Map<LocalDate, List<String>> pinMap = identGeneratorService.genererIdenterMap(firstDate, lastDate, type, syntetiskIdent);

        var antall = filterIdents(antallPerDag, pinMap, numberOfIdents, syntetiskIdent);
        log.info("Ajourhold: år {} {} {} identer har fått allokert antall nye: {}", year, type, syntetiskIdent ? NAV_SYNTETISKE : VANLIGE, antall);

        return antall;
    }

    private int adjustForYear(int year, int antallPerDag) {

        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG) {
            antallPerDag = MIN_ANTALL_IDENTER_PER_DAG;
        }

        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR) {
            var dateOfYear = LocalDate.of(year, 1, 1);

            if (dateOfYear.isBefore(LocalDate.now()) && ChronoUnit.YEARS.between(dateOfYear, LocalDate.now()) <= 3) {
                return MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR;
            }
        }
        return antallPerDag;
    }

    private int filterIdents(int identsPerDay, Map<LocalDate, List<String>> pinMap, int numberOfIdents, boolean syntetiskIdent) {

        var identsNotInDatabase = filterAgainstDatabase(identsPerDay, pinMap);
        List<String> ledig = new ArrayList<>();

        if (!syntetiskIdent) {
            var tpsStatus = tpsMessagingConsumer.getIdenterStatuser(identsNotInDatabase);

            var rekvirert = tpsStatus.stream()
                    .filter(TpsStatusDTO::isInUse)
                    .map(TpsStatusDTO::getIdent)
                    .toList();

            saveIdents(rekvirert, Rekvireringsstatus.I_BRUK, "TPS");

            ledig.addAll(tpsStatus.stream()
                    .filter(i -> !i.isInUse())
                    .map(TpsStatusDTO::getIdent)
                    .toList());
        } else {
            ledig.addAll(identsNotInDatabase.stream()
                    .toList());
        }

        if (ledig.size() > numberOfIdents) {
            Collections.shuffle(ledig);
            ledig = ledig.subList(0, numberOfIdents);
        }

        var lagredeIdenter = saveIdents(ledig, LEDIG, null);
        return lagredeIdenter.size();
    }

    private Set<String> filterAgainstDatabase(int antallPerDag, Map<LocalDate, List<String>> pinMap) {

        return pinMap.entrySet().stream()
                .map(entry -> entry.getValue().stream()
                        .map(value -> identRepository.existsByPersonidentifikator(value) ? null : value)
                        .filter(Objects::nonNull)
                        .limit(antallPerDag)
                        .collect(Collectors.toSet())
                )
                .flatMap(Collection::stream)
                .collect(Collectors.toSet());
    }

    private List<Ident> saveIdents(
            List<String> idents,
            Rekvireringsstatus status,
            String rekvirertAv) {

        return idents.stream()
                .map(ident -> identRepository.save(IdentGeneratorUtil.createIdent(ident, status, rekvirertAv)))
                .toList();
    }

    /**
     * Fjerner FNR/DNR/BNR fra ident-pool-databasen som finnes i prod
     */
    public void getIdentsAndCheckProd() {

        HentIdenterRequest request = HentIdenterRequest.builder()
                .antall(MAX_SIZE_TPS_QUEUE)
                .foedtEtter(LocalDate.of(1850, 1, 1))
                .build();

        var firstPage = identRepository.findAll(LEDIG, request.getFoedtEtter(), PageRequest.of(0, MAX_SIZE_TPS_QUEUE));
        var usedIdents = new ArrayList<String>();

        if (firstPage.getTotalPages() > 0) {
            for (int i = 0; i < firstPage.getTotalPages(); i++) {
                Page<Ident> page = identRepository.findAll(LEDIG, request.getFoedtEtter(), PageRequest.of(i, MAX_SIZE_TPS_QUEUE));

                var idents = page.getContent().stream()
                        .map(Ident::getPersonidentifikator)
                        .collect(Collectors.toSet());

                try {
                    var identStatus = tpsMessagingConsumer.getIdenterProdStatus(idents);
                    usedIdents.addAll(identStatus.stream()
                            .filter(TpsStatusDTO::isInUse)
                            .map(TpsStatusDTO::getIdent)
                            .toList());

                } catch (ResponseStatusException e) {
                    log.error("Kunne ikke hente status fra TPS", e);
                }
            }
        }
        if (!usedIdents.isEmpty()) {
            log.info("Fjerner {} identer som er i bruk i prod, men som er markert som LEDIG i ident-pool.", usedIdents.size());

            usedIdents.forEach(usedIdent -> {
                Ident ident = identRepository.findTopByPersonidentifikator(usedIdent);
                ident.setRekvireringsstatus(Rekvireringsstatus.I_BRUK);
                ident.setRekvirertAv("TPS");
                identRepository.save(ident);
            });
        }
    }
}
