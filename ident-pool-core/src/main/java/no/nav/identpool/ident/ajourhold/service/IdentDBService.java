package no.nav.identpool.ident.ajourhold.service;

import static java.time.LocalDate.now;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import no.nav.identpool.ident.ajourhold.tps.generator.FnrGenerator;
import no.nav.identpool.ident.ajourhold.util.IdentDistribusjon;
import no.nav.identpool.ident.ajourhold.util.PersonIdentifikatorUtil;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentRepository;

@Service
@RequiredArgsConstructor
class IdentDBService {

    private final IdentMQService mqService;
    private final IdentRepository identRepository;
    private final IdentDistribusjon identDistribusjon;

    private LocalDate current;

    private static IdentEntity createPinIdent(String fnr, Rekvireringsstatus status) {
        return createIdent(fnr, status, Identtype.FNR);
    }

    private static IdentEntity createDnrIdent(String fnr, Rekvireringsstatus status) {
        return createIdent(fnr, status, Identtype.DNR);
    }

    private static IdentEntity createIdent(String fnr, Rekvireringsstatus status, Identtype type) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .foedselsdato(PersonIdentifikatorUtil.toBirthdate(fnr))
                .rekvireringsstatus(status)
                .identtype(type)
                .build();
    }

    void checkCriticalAndGenerate() {
        current = now();
        int minYearMinus = 110;
        LocalDate minDate = LocalDate.of(current.getYear() - minYearMinus, 1, 1);
        while (minDate.isBefore(current)) {
            checkAndGenerateForDate(minDate, this::checkCriticalForYearPin, FnrGenerator::genererIdenterMap, IdentDBService::createPinIdent);
            checkAndGenerateForDate(minDate, this::checkCriticalForYearDnr, FnrGenerator::genererIdenterDnrMap, IdentDBService::createDnrIdent);
            minDate = minDate.plusDays(1);
        }
    }

    private void checkAndGenerateForDate(LocalDate date,
            Function<Integer, Boolean> checkCritical,
            BiFunction<LocalDate, LocalDate, Map<LocalDate, List<String>>> pinGenerator,
            BiFunction<String, Rekvireringsstatus, IdentEntity> identCreator) {
        for (int i = 0; i < 3; ++i) {
            if (checkCritical.apply(date.getYear())) {
                generateForYear(date.getYear(), pinGenerator, identCreator);
            } else {
                break;
            }
        }
    }

    private void generateForYear(int year,
            BiFunction<LocalDate, LocalDate, Map<LocalDate, List<String>>> pinGenerator,
            BiFunction<String, Rekvireringsstatus, IdentEntity> identCreator) {
        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year, 12, 31);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year + 1) * 2;
        Map<LocalDate, List<String>> pinMap = pinGenerator.apply(firstDate, lastDate.plusDays(1));

        List<String> filtered = filterDatabse(antallPerDag, pinMap);
        checkTpsAndStore(filtered, identCreator);
    }

    private List<String> filterDatabse(int antallPerDag, Map<LocalDate, List<String>> pinMap) {
        final List<String> arrayList = new ArrayList<>(antallPerDag * pinMap.size());
        pinMap.forEach((ignored, value) -> {
            ArrayList<String> local = new ArrayList<>(antallPerDag);
            Iterator<String> iterator = value.iterator();
            while (iterator.hasNext() && local.size() < antallPerDag) {
                String pin = iterator.next();
                if (!identRepository.existsByPersonidentifikator(pin)) {
                    local.add(pin);
                }
            }
            arrayList.addAll(local);
        });
        return arrayList;
    }

    private void checkTpsAndStore(List<String> filtered, BiFunction<String, Rekvireringsstatus, IdentEntity> identCreator) {

        Map<String, Boolean> identerIBruk = mqService.fnrsExists(filtered);
        storeIdenter(identerIBruk.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()),
                Rekvireringsstatus.I_BRUK, identCreator);
        storeIdenter(identerIBruk.entrySet().stream()
                        .filter(x -> !x.getValue())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()),
                Rekvireringsstatus.LEDIG, identCreator);
    }

    private void storeIdenter(List<String> pins, Rekvireringsstatus status, BiFunction<String, Rekvireringsstatus, IdentEntity> identCreator) {
        identRepository.saveAll(pins
                .stream()
                .map(fnr -> identCreator.apply(fnr, status))
                .collect(Collectors.toList()));
    }

    private boolean checkCriticalForYearPin(int year) {
        return criticalForYear(year, Identtype.FNR);
    }

    private boolean checkCriticalForYearDnr(int year) {
        return criticalForYear(year, Identtype.DNR);
    }

    private boolean criticalForYear(int year, Identtype type) {
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year);
        int days = year == current.getYear() ? 365 - current.getDayOfYear() : 365;
        long count = identRepository.countByFoedselsdatoBetweenAndIdenttypeAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                type,
                Rekvireringsstatus.LEDIG);
        return count < antallPerDag * days;
    }
}
