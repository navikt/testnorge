package no.nav.identpool.ajourhold;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.identpool.consumers.TpsfConsumer;
import no.nav.identpool.domain.Ident;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.rs.v1.support.HentIdenterRequest;
import no.nav.identpool.service.IdentGeneratorService;
import no.nav.identpool.service.IdentTpsService;
import no.nav.identpool.util.IdentGeneratorUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
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

    private static final int MAX_SIZE_TPS_QUEUE = 80;
    private static final int MIN_ANTALL_IDENTER_PER_DAG = 2;
    private static final int MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR = 10;

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final IdentTpsService identTpsService;
    private final TpsfConsumer tpsfConsumer;

    private final Counter counter;

    LocalDate current;
    private int newIdentCount;

    public boolean checkCriticalAndGenerate() {

        newIdentCount = 0;
        current = LocalDate.now();

        int minYearMinus = 110;
        LocalDate minDate = current.minusYears(minYearMinus).with(firstDayOfYear());
        while (minDate.isBefore(current.plusYears(1))) {
            checkAndGenerateForDate(minDate, Identtype.FNR);
            checkAndGenerateForDate(minDate, Identtype.DNR);
            checkAndGenerateForDate(minDate, Identtype.BOST);
            minDate = minDate.plusYears(1);
        }
        counter.increment(newIdentCount);
        return newIdentCount > 0;
    }

    void checkAndGenerateForDate(
            LocalDate date,
            Identtype type) {

        int maxRuns = 3;
        int runs = 0;
        while (runs < maxRuns) {
            int numberOfMissingIdents = getNumberOfMissingIdents(date.getYear(), type);
            if (numberOfMissingIdents > 0) {
                generateForYear(date.getYear(), type, numberOfMissingIdents);
            } else {
                break;
            }
            runs++;
        }
    }

    private int getNumberOfMissingIdents(
            int year,
            Identtype type) {

        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year);
        antallPerDag = adjustForYear(year, antallPerDag);
        int days = (year == current.getYear() ? 365 - (365 - current.getDayOfYear()) : 365);
        long count = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                type,
                LEDIG);
        return Math.toIntExact((antallPerDag * days) - count);
    }

    void generateForYear(
            int year,
            Identtype type,
            int numberOfIdents) {

        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year + 1) * 2;
        antallPerDag = adjustForYear(year, antallPerDag);

        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year + 1, 1, 1);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }
        if (lastDate.isEqual(firstDate)) {
            lastDate = lastDate.plusDays(1);
        }

        Map<LocalDate, List<String>> pinMap = identGeneratorService.genererIdenterMap(firstDate, lastDate, type);

        filterIdents(antallPerDag, pinMap, numberOfIdents);
    }

    private int adjustForYear(
            int year,
            int antallPerDag) {

        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG) {
            antallPerDag = MIN_ANTALL_IDENTER_PER_DAG;
        }
        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR) {
            LocalDate dateOfYear = LocalDate.of(year, 1, 1);
            if (dateOfYear.isBefore(LocalDate.now()) && ChronoUnit.YEARS.between(dateOfYear, LocalDate.now()) <= 3) {
                return MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR;
            }
        }
        return antallPerDag;
    }

    private void filterIdents(
            int identsPerDay,
            Map<LocalDate, List<String>> pinMap,
            int numberOfIdents) {

        Set<String> identsNotInDatabase = filterAgainstDatabase(identsPerDay, pinMap);
        Set<TpsStatus> tpsStatuses = identTpsService.checkIdentsInTps(identsNotInDatabase);

        List<String> rekvirert = tpsStatuses.stream()
                .filter(TpsStatus::isInUse)
                .map(TpsStatus::getIdent)
                .collect(Collectors.toList());

        newIdentCount += rekvirert.size();
        saveIdents(rekvirert, Rekvireringsstatus.I_BRUK, "TPS");

        List<String> ledig = tpsStatuses.stream()
                .filter(i -> !i.isInUse())
                .map(TpsStatus::getIdent)
                .collect(Collectors.toList());

        if (ledig.size() > numberOfIdents) {
            Collections.shuffle(ledig);
            ledig = ledig.subList(0, numberOfIdents);
        }

        newIdentCount += ledig.size();
        saveIdents(ledig, LEDIG, null);
    }

    private Set<String> filterAgainstDatabase(
            int antallPerDag,
            Map<LocalDate, List<String>> pinMap) {

        final Set<String> dbCleaned = new HashSet<>();
        pinMap.forEach((d, value) -> dbCleaned.addAll(
                value.stream()
                        .map(v -> identRepository.existsByPersonidentifikator(v) ? null : v)
                        .filter(Objects::nonNull)
                        .limit(antallPerDag)
                        .collect(Collectors.toSet())
        ));
        return dbCleaned;
    }

    private List<Ident> saveIdents(
            List<String> idents,
            Rekvireringsstatus status,
            String rekvirertAv) {

        return idents.stream()
                .map(ident -> identRepository.save(IdentGeneratorUtil.createIdent(ident, status, rekvirertAv)))
                .collect(Collectors.toList());
    }

    /**
     * Fjerner FNR/DNR/BNR fra ident-pool-databasen som finnes i prod
     */
    public void getIdentsAndCheckProd() {

        HentIdenterRequest request = HentIdenterRequest.builder()
                .antall(MAX_SIZE_TPS_QUEUE)
                .foedtEtter(LocalDate.of(1850, 1, 1))
                .build();
        Page<Ident> firstPage = identRepository.findAll(LEDIG, request.getFoedtEtter(), PageRequest.of(0, MAX_SIZE_TPS_QUEUE));
        List<String> usedIdents = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (firstPage.getTotalPages() > 0) {
            for (int i = 0; i < firstPage.getTotalPages(); i++) {
                Page<Ident> page = identRepository.findAll(LEDIG, request.getFoedtEtter(), PageRequest.of(i, MAX_SIZE_TPS_QUEUE));

                List<String> idents = page.getContent().stream().map(Ident::getPersonidentifikator).collect(Collectors.toList());
                try {
                    JsonNode statusFromTps = tpsfConsumer.getProdStatusFromTps(idents).findValue("EFnr");
                    List<Map<String, Object>> identStatus = objectMapper.convertValue(statusFromTps, new TypeReference<List<Map<String, Object>>>() {
                    });
                    for (Map<String, Object> map : identStatus) {
                        if (!map.containsKey("svarStatus")) {
                            usedIdents.add(String.valueOf(map.get("fnr")));
                        }
                    }
                } catch (IOException e) {
                    log.error("Kunne ikke hente status fra TPS", e);
                }
            }
        }
        if (!usedIdents.isEmpty()) {
            log.info("Fjerner identer som er i bruk i prod, men som er markert som LEDIG i ident-pool.");

            for (String usedIdent : usedIdents) {
                Ident ident = identRepository.findTopByPersonidentifikator(usedIdent);
                ident.setRekvireringsstatus(Rekvireringsstatus.I_BRUK);
                ident.setRekvirertAv("TPS");
                identRepository.save(ident);
            }
        }
    }
}
