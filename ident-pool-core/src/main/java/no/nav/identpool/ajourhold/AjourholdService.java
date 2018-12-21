package no.nav.identpool.ajourhold;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.domain.TpsStatus;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.service.IdentGeneratorService;
import no.nav.identpool.service.IdentTpsService;
import no.nav.identpool.util.IdentDistribusjonUtil;
import no.nav.identpool.util.IdentGeneratorUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AjourholdService {

    private final IdentRepository identRepository;
    private final IdentGeneratorService identGeneratorService;
    private final IdentTpsService identTpsService;

    LocalDate current;
    private int newIdentCount;

    public boolean checkCriticalAndGenerate() {
        newIdentCount = 0;
        current = LocalDate.now();

        int minYearMinus = 110;
        LocalDate minDate = current.minusYears(minYearMinus).with(firstDayOfYear());
        while (minDate.isBefore(current)) {
            checkAndGenerateForDate(minDate, Identtype.FNR);
            checkAndGenerateForDate(minDate, Identtype.DNR);
            minDate = minDate.plusYears(1);
        }

        return newIdentCount > 0;
    }

    void checkAndGenerateForDate(LocalDate date, Identtype type) {
        int maxRuns = 3;
        int runs = 0;
        while (criticalForYear(date.getYear(), type) && runs < maxRuns) {
            generateForYear(date.getYear(), type);
            runs++;
        }
    }

    private boolean criticalForYear(int year, Identtype type) {
        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year);
        int days = (year == current.getYear() ? 365 - current.getDayOfYear() : 365);
        long count = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                type,
                Rekvireringsstatus.LEDIG);
        return count < antallPerDag * days;
    }

    void generateForYear(int year, Identtype type) {
        int antallPerDag = IdentDistribusjonUtil.antallPersonerPerDagPerAar(year + 1) * 2;

        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year + 1, 1, 1);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }

        Map<LocalDate, List<String>> pinMap = identGeneratorService.genererIdenterMap(firstDate, lastDate, type);

        filterIdents(antallPerDag, pinMap);
    }

    private void filterIdents(int antallPerDag, Map<LocalDate, List<String>> pinMap) {
        List<String> filtered = filterAgainstDatabase(antallPerDag, pinMap);
        Set<TpsStatus> identsInUse = identTpsService.checkIdentsInTps(filtered);

        List<String> rekvirert = identsInUse.stream()
                .filter(TpsStatus::isInUse)
                .map(TpsStatus::getIdent)
                .collect(Collectors.toList());

        newIdentCount += rekvirert.size();
        saveIdents(rekvirert, Rekvireringsstatus.I_BRUK, "TPS");

        List<String> ledig = identsInUse.stream()
                .filter(i -> !i.isInUse())
                .map(TpsStatus::getIdent)
                .collect(Collectors.toList());

        newIdentCount += ledig.size();
        saveIdents(ledig, Rekvireringsstatus.LEDIG, null);
    }

    private List<String> filterAgainstDatabase(int antallPerDag, Map<LocalDate, List<String>> pinMap) {
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

    private void saveIdents(List<String> pins, Rekvireringsstatus status, String rekvirertAv) {
        identRepository.saveAll(pins.stream()
                .map(fnr -> IdentGeneratorUtil.createIdent(fnr, status, rekvirertAv))
                .collect(Collectors.toList()));
    }
}
