package no.nav.identpool.ident.ajourhold.service;

import static java.time.LocalDate.now;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

    private static IdentEntity createDefaultIdent(String fnr, Rekvireringsstatus status) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .foedselsdato(PersonIdentifikatorUtil.toBirthdate(fnr))
                .rekvireringsstatus(status)
                .identtype(Identtype.FNR)
                .build();
    }

    void checkCriticalAndGenerate() {
        current = now();
        int minYearMinus = 110;
        LocalDate minDate = LocalDate.of(current.getYear() - minYearMinus, 1, 1);
        while (minDate.isBefore(current)) {
            checkAndGenerateForDate(current);
            current = current.plusDays(1);
        }
    }

    private void checkAndGenerateForDate(LocalDate date) {
        for (int i = 0; i < 3; ++i) {
            if (!criticalForYear(date.getYear())) {
                generateForYear(date.getYear());
            }
        }
    }

    private void generateForYear(int year) {
        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year, 12, 31);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year + 1) * 2;
        Map<LocalDate, List<String>> pinMap = FnrGenerator.genererIdenterMap(firstDate, lastDate.plusDays(1));

        List<String> filtered = filterDatabse(antallPerDag, pinMap);
        checkTpsAndStore(filtered);
    }

    private List<String> filterDatabse(int antallPerDag, Map<LocalDate, List<String>> pinMap) {
        final List<String> arrayList = new ArrayList<>(antallPerDag * pinMap.size());
        pinMap.forEach((ignored, value) -> {
            ArrayList<String> local = new ArrayList<>(antallPerDag);
            Iterator<String> iterator = value.iterator();
            while (iterator.hasNext() && local.size() < antallPerDag) {
                String personidentifikator = iterator.next();
                if (!identRepository.existsByPersonidentifikator(personidentifikator)) {
                    local.add(personidentifikator);
                }
            }
            arrayList.addAll(local);
        });
        return arrayList;
    }

    private void checkTpsAndStore(List<String> filtered) {
        Map<String, Boolean> identerIBruk = mqService.fnrsExists(filtered);
        storeIdenter(identerIBruk.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()), Rekvireringsstatus.I_BRUK);
        storeIdenter(identerIBruk.entrySet().stream()
                .filter(x -> !x.getValue())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList()), Rekvireringsstatus.LEDIG);
    }

    private void storeIdenter(List<String> pins, Rekvireringsstatus status) {
        identRepository.saveAll(pins
                .stream()
                .map(fnr -> createDefaultIdent(fnr, status))
                .collect(Collectors.toList()));
    }

    private boolean criticalForYear(int year) {
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year);
        int days = year == current.getYear() ? 365 - current.getDayOfYear() : 365;
        long count = identRepository.countByFoedselsdatoBetweenAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                Rekvireringsstatus.LEDIG);
        return count < antallPerDag * days;
    }
}
