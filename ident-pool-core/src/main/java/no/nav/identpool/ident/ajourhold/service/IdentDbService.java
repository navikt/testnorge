package no.nav.identpool.ident.ajourhold.service;

import static java.time.LocalDate.now;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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

    private final IdentMQService mqService;
    private final IdentRepository identRepository;
    private final IdentDistribusjon identDistribusjon;

    private LocalDate current;

    void checkCritcalAndGenerate() {
        current = now();
        int minYearMinus = 110;
        LocalDate minDate = LocalDate.of(current.getYear() - minYearMinus, 1, 1);
        int counter = 0;
        while (minDate.isBefore(current)) {
            if (counter == 3 || !criticalForYear(minDate.getYear())) {
                counter = 0;
                minDate = minDate.plusYears(1);
            } else {
                generateForYear(minDate.getYear());
                counter += 1;
            }
        }
    }

    private void generateForYear(int year) {
        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year, 12, 31);

        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }

        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year);

        Map<LocalDate, List<String>> fnrMap = FnrGenerator.genererIdenterMap(firstDate, lastDate.plusDays(1));
        String[][] filtered = filterDatabse(antallPerDag, fnrMap);
        int paralellThreads = 2;
        for (int i = 0; i < filtered.length; i += paralellThreads) {
            int threads = paralellThreads;
            if (i + paralellThreads > filtered.length) {
                threads = filtered.length - i;
            }
            checkMqStore(i, threads, filtered);
        }
    }

    private String[][] filterDatabse(int antallPerDag, Map<LocalDate, List<String>> fnrMap) {
        List<ArrayList<String>> fnrArray = new ArrayList<>();
        final ArrayList<String> currentArray = new ArrayList<>(501);
        fnrMap.forEach((date, fnrs) -> {
            if (identRepository.countByFoedselsdato(date) == fnrs.size()) {
                return;
            }
            int count = 0;
            for (int i = 0; i < fnrs.size() && count < antallPerDag * 3; ++i) {
                String fnr = fnrs.get(i);
                if (identRepository.existsByPersonidentifikator(fnr)) {
                    continue;
                }
                if (currentArray.size() == 500) {
                    fnrArray.add(new ArrayList<>(currentArray));
                    currentArray.clear();
                }
                currentArray.add(fnr);
                count += 1;

            }
        });
        return fnrArray.stream().map(array -> array.toArray(new String[0])).toArray(String[][]::new);
    }

    private void checkMqStore(int startIndex, int numberOfThreads, String[]... fnrs) {

        String[][] fnrsArray = new String[numberOfThreads][];
        System.arraycopy(fnrs, startIndex, fnrsArray, 0, numberOfThreads);

        boolean[][] identerIBruk = mqService.fnrsExistsArray(fnrsArray);
        for (int i = 0; i < identerIBruk.length; ++i) {
            storeIdenter(identerIBruk[i], fnrs[i]);
        }
    }

    private void storeIdenter(boolean[] identerIBruk, String... identer) {
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

    private boolean criticalForYear(int year) {
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year);
        int days = year == current.getYear() ? 365 - current.getDayOfYear() : 365;
        long count = identRepository.countByFoedselsdatoBetweenAndRekvireringsstatus(
                LocalDate.of(year, 1, 1),
                LocalDate.of(year + 1, 1, 1),
                Rekvireringsstatus.LEDIG);
        return count < antallPerDag * days;
    }

    private static IdentEntity createDefaultIdent(String fnr, Rekvireringsstatus status) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .foedselsdato(PersonIdentifikatorUtil.toBirthdate(fnr))
                .rekvireringsstatus(status)
                .identtype(Identtype.FNR)
                .build();
    }
}
