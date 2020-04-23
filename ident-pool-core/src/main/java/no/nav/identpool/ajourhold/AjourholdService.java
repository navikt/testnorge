package no.nav.identpool.ajourhold;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static no.nav.identpool.domain.Rekvireringsstatus.LEDIG;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.querydsl.core.types.Predicate;
import io.micrometer.core.instrument.Counter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
import no.nav.identpool.util.IdentPredicateUtil;

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
            Identtype type
    ) {
        int maxRuns = 3;
        int runs = 0;
        while (runs < maxRuns && criticalForYear(date.getYear(), type)) {
            generateForYear(date.getYear(), type);
            runs++;
        }
    }

    private boolean criticalForYear(
            int year,
            Identtype type
    ) {
        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year);
        antallPerDag = adjustForYear(year, antallPerDag);
        int days = (year == current.getYear() ? 365 - current.getDayOfYear() : 365);
        long count = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                type,
                LEDIG);
        return count < antallPerDag * days;
    }

    void generateForYear(
            int year,
            Identtype type
    ) {
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
        if (year > 2018) {
            log.info("generateForYear: firstDate: {}, lastDate:{}", firstDate.toString(), lastDate.toString());
        }

        Map<LocalDate, List<String>> pinMap = identGeneratorService.genererIdenterMap(firstDate, lastDate, type);

        filterIdents(antallPerDag, pinMap);
    }

    private int adjustForYear(
            int year,
            int antallPerDag
    ) {
        log.info("adjustForYear: year: {}. antallPerDag: {}", year, antallPerDag);
        if (antallPerDag < MIN_ANTALL_IDENTER_PER_DAG) {
            LocalDate dateOfYear = LocalDate.of(year, 1, 1);
            if (dateOfYear.isBefore(LocalDate.now()) && ChronoUnit.YEARS.between(dateOfYear, LocalDate.now()) <= 3) {
                log.info("Genererer flere identer per dag for år {}", year);
                return MIN_ANTALL_IDENTER_PER_DAG_SENERE_AAR;
            }
            return MIN_ANTALL_IDENTER_PER_DAG;
        }
        return antallPerDag;
    }

    private void filterIdents(
            int antallPerDag,
            Map<LocalDate, List<String>> pinMap
    ) {
        List<String> identsNotInDatabase = filterAgainstDatabase(antallPerDag, pinMap);
        Set<TpsStatus> tpsStatuses = identTpsService.checkIdentsInTps(identsNotInDatabase, new ArrayList<>());

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

        newIdentCount += ledig.size();
        saveIdents(ledig, LEDIG, null);
    }

    private List<String> filterAgainstDatabase(
            int antallPerDag,
            Map<LocalDate, List<String>> pinMap
    ) {
        final List<String> arrayList = new ArrayList<>();
        pinMap.forEach((d, value) -> arrayList.addAll(
                value.stream()
                        .map(v -> identRepository.existsByPersonidentifikator(v) ? null : v)
                        .filter(Objects::nonNull)
                        .limit(antallPerDag)
                        .collect(Collectors.toList())
        ));
        return arrayList;
    }

    private List<Ident> saveIdents(
            List<String> idents,
            Rekvireringsstatus status,
            String rekvirertAv
    ) {
        return identRepository.saveAll(idents.stream()
                .map(fnr -> IdentGeneratorUtil.createIdent(fnr, status, rekvirertAv))
                .collect(Collectors.toList()));
    }

    /**
     * Fjerner FNR/DNR/BNR fra ident-pool-databasen som finnes i prod
     */
    public void getIdentsAndCheckProd() {
        HentIdenterRequest request = HentIdenterRequest.builder()
                .antall(MAX_SIZE_TPS_QUEUE)
                .foedtEtter(LocalDate.of(1850, 1, 1))
                .build();
        Predicate predicate = IdentPredicateUtil.lagPredicateFraRequest(request, LEDIG);
        Page<Ident> firstPage = identRepository.findAll(predicate, PageRequest.of(0, MAX_SIZE_TPS_QUEUE));
        List<String> usedIdents = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();

        if (firstPage.getTotalPages() > 0) {
            for (int i = 0; i < firstPage.getTotalPages(); i++) {
                Page<Ident> page = identRepository.findAll(predicate, PageRequest.of(i, MAX_SIZE_TPS_QUEUE));

                List<String> idents = page.getContent().stream().map(Ident::getPersonidentifikator).collect(Collectors.toList());
                try {
                    JsonNode statusFromTps = tpsfConsumer.getStatusFromTps(idents).findValue("EFnr");
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
