package no.nav.identpool.ajourhold.service;

import static java.time.temporal.TemporalAdjusters.firstDayOfYear;
import static no.nav.identpool.util.PersonidentifikatorUtil.getKjonn;
import static no.nav.identpool.util.PersonidentifikatorUtil.toBirthdate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.identpool.ajourhold.tps.generator.IdentGenerator;
import no.nav.identpool.ajourhold.util.IdentDistribusjon;
import no.nav.identpool.domain.Identtype;
import no.nav.identpool.domain.Rekvireringsstatus;
import no.nav.identpool.repository.IdentEntity;
import no.nav.identpool.repository.IdentRepository;
import no.nav.identpool.service.IdentMQService;
import no.nav.identpool.util.PersonidentifikatorUtil;

@Service
@RequiredArgsConstructor
public class IdentDBService {

    private final IdentMQService mqService;
    private final IdentRepository identRepository;
    private final IdentDistribusjon identDistribusjon;

    LocalDate current;

    private int newIdentCount;

    boolean checkCriticalAndGenerate() {
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

    public void saveIdents(List<String> pins, Rekvireringsstatus status, String rekvirertAv) {
        identRepository.saveAll(pins.stream()
                .map(fnr -> createIdent(fnr, status, PersonidentifikatorUtil.getPersonidentifikatorType(fnr), rekvirertAv))
                .collect(Collectors.toList()));
    }

    void checkAndGenerateForDate(LocalDate date, Identtype type) {
        int maxRuns = 3;
        int runs = 0;
        while (criticalForYear(date.getYear(), type) && runs < maxRuns) {
            generateForYear(date.getYear(), type);
            runs++;
        }
    }

    void generateForYear(int year, Identtype type) {
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year + 1) * 2;

        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year + 1, 1, 1);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }

        Map<LocalDate, List<String>> pinMap = IdentGenerator.genererIdenterMap(firstDate, lastDate, type);

        filterIdents(antallPerDag, pinMap);
    }

    private void filterIdents(int antallPerDag, Map<LocalDate, List<String>> pinMap) {
        List<String> filtered = filterAgainstDatabase(antallPerDag, pinMap);
        Map<String, Boolean> identerIBruk = mqService.checkInTps(filtered);

        List<String> rekvirert = identerIBruk.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        newIdentCount += rekvirert.size();
        saveIdents(rekvirert, Rekvireringsstatus.I_BRUK, "TPS");

        List<String> ledig = identerIBruk.entrySet().stream()
                .filter(x -> !x.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

        newIdentCount += ledig.size();
        saveIdents(ledig, Rekvireringsstatus.LEDIG, null);
    }

    private boolean criticalForYear(int year, Identtype type) {
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year);
        int days = (year == current.getYear() ? 365 - current.getDayOfYear() : 365);
        long count = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                type,
                Rekvireringsstatus.LEDIG);
        return count < antallPerDag * days;
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

    private IdentEntity createIdent(String fnr, Rekvireringsstatus status, Identtype type, String rekvirertAv) {
        return IdentEntity.builder()
                .finnesHosSkatt(false)
                .personidentifikator(fnr)
                .foedselsdato(toBirthdate(fnr))
                .kjoenn(getKjonn(fnr))
                .rekvireringsstatus(status)
                .rekvirertAv(rekvirertAv)
                .identtype(type)
                .build();
    }
}
