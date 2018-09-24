package no.nav.identpool.ident.ajourhold.service;

import static java.time.LocalDate.now;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
public class IdentDbService {

    private final int minYearMinus = 110;
    private final int paralellThreads = 2;

    private final IdentMQService mqService;
    private final IdentRepository identRepository;
    private final IdentDistribusjon identDistribusjon;

    public void checkCritcalAndGenerate() {
        LocalDate current = now();
        LocalDate minDate = LocalDate.of(current.getYear() - minYearMinus, 1, 1);
        while(minDate.isBefore(current)) {
            generateForYear(minDate.getYear());
        }
    }

    private void generateForYear(int year) {
        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate current = now();
        LocalDate lastDate = LocalDate.of(year, 12, 31);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year);
        if (!criticalForYear(year, antallPerDag)) return;

        String[][] fnrs = FnrGenerator.genererIdenter(firstDate, lastDate.plusDays(1));
        String[][] filtered = filterDatabse(antallPerDag, fnrs);
        for (int i = 0; i < fnrs.length; ++i) {
            int threads = paralellThreads;
            if (i + paralellThreads > filtered.length) {
                threads = filtered.length - threads;
            }
            String[][] copy = new String[threads][];
            System.arraycopy(filtered, i, copy, 0, threads);
            checkMqStore(copy);
        }
    }

    private String[][] filterDatabse(int antallPerDag, String[]... fnrs) {
        List<ArrayList<String>> fnrArray = new ArrayList<>();
        ArrayList<String> currentArray = new ArrayList<>(501);
        for (String[] array: fnrs) {
            int count = 0;
            for (int i = 0; i < array.length && count < antallPerDag * 3; ++i)  {
                if (!identRepository.existsByPersonidentifikator(array[i])) {
                    if (currentArray.size() == 500) {
                        fnrArray.add(currentArray);
                        currentArray = new ArrayList<>(501);
                    }
                    currentArray.add(array[i]);
                }
                count += 1;
            }
        }
        fnrArray.add(currentArray);
        return fnrArray.stream().map(i -> i.toArray(new String[0])).toArray(String[][]::new);
    }

    private void checkMqStore(String[]... fnrs) {
    }

    private void storeIdenter(boolean[] identerIBruk, String[] identer) {
        identRepository.saveAll(
                IntStream.range(0, identer.length)
                        .filter(j -> !identerIBruk[j])
                        .mapToObj(j -> createDefaultIdent(identer[j], Rekvireringsstatus.LEDIG))
                        .collect(Collectors.toList()));
        identRepository.saveAll(
                IntStream.range(0, identer.length)
                        .filter(j -> identerIBruk[j])
                        .mapToObj(j -> createDefaultIdent(identer[j], Rekvireringsstatus.I_BRUK))
                        .collect(Collectors.toList()));
    }

    private boolean criticalForYear(int year, int antallPerDag) {
        LocalDate current = now();
        int days = year == current.getYear() ? 365 - current.getDayOfYear() : 365;
        long count = identRepository.countByFodselsdatoBetweenAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                Rekvireringsstatus.LEDIG);
        return count < antallPerDag * days;
    }

    private static IdentEntity createDefaultIdent(String fnr, Rekvireringsstatus status) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .fodselsdato(PersonIdentifikatorUtil.toBirthdate(fnr))
                .rekvireringsstatus(status)
                .identtype(Identtype.FNR)
                .build();
    }
}
