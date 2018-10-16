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
import no.nav.identpool.ident.ajourhold.tps.generator.IdentGenerator;
import no.nav.identpool.ident.ajourhold.util.IdentDistribusjon;
import no.nav.identpool.ident.domain.Identtype;
import no.nav.identpool.ident.domain.Rekvireringsstatus;
import no.nav.identpool.ident.repository.IdentEntity;
import no.nav.identpool.ident.repository.IdentRepository;
import no.nav.identpool.ident.service.IdentMQService;
import no.nav.identpool.util.PersonidentifikatorUtil;

@Service
@RequiredArgsConstructor
public class IdentDBService {

    private final IdentMQService mqService;
    private final IdentRepository identRepository;
    private final IdentDistribusjon identDistribusjon;

    private LocalDate current;

    void checkCriticalAndGenerate() {
        current = now();
        int minYearMinus = 110;
        LocalDate minDate = LocalDate.of(current.getYear() - minYearMinus, 1, 1);
        while (minDate.isBefore(current)) {
            checkAndGenerateForDate(minDate, Identtype.FNR);
            checkAndGenerateForDate(minDate, Identtype.DNR);
            minDate = minDate.plusYears(1);
        }
    }

    private void checkAndGenerateForDate(LocalDate date, Identtype type) {
        for (int i = 0; i < 3; ++i) {
            if (criticalForYear(date.getYear(), type)) {
                generateForYear(date.getYear(), type);
            } else {
                break;
            }
        }
    }

    private void generateForYear(int year, Identtype type) {

        LocalDate firstDate = LocalDate.of(year, 1, 1);
        LocalDate lastDate = LocalDate.of(year, 12, 31);
        if (lastDate.isAfter(current)) {
            lastDate = LocalDate.of(year, current.getMonth(), current.getDayOfMonth());
        }
        int antallPerDag = identDistribusjon.antallPersonerPerDagPerAar(year + 1) * 2;
        Map<LocalDate, List<String>> pinMap = IdentGenerator.genererIdenterMap(firstDate, lastDate, type);

        List<String> filtered = filterDatabase(antallPerDag, pinMap);

        Map<String, Boolean> identerIBruk = mqService.finnesITps(filtered);

        lagre(identerIBruk);
    }

    private List<String> filterDatabase(int antallPerDag, Map<LocalDate, List<String>> pinMap) {
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

    public void lagre(Map<String, Boolean> identerIBruk) {

        lagreIdenter(identerIBruk.entrySet().stream()
                        .filter(Map.Entry::getValue)
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()),
                Rekvireringsstatus.I_BRUK, "TPS");
        lagreIdenter(identerIBruk.entrySet().stream()
                        .filter(x -> !x.getValue())
                        .map(Map.Entry::getKey)
                        .collect(Collectors.toList()),
                Rekvireringsstatus.LEDIG, null);
    }

    public void lagreIdenter(List<String> pins, Rekvireringsstatus status, String rekvirertAv) {
        identRepository.saveAll(pins.stream()
                .map(fnr -> createIdent(fnr, status, Integer.parseInt(fnr.substring(0, 1)) > 3 ? Identtype.DNR : Identtype.FNR, rekvirertAv))
                .collect(Collectors.toList()));
    }

    private IdentEntity createIdent(String fnr, Rekvireringsstatus status, Identtype type, String rekvirertAv) {
        return IdentEntity.builder()
                .finnesHosSkatt("0")
                .personidentifikator(fnr)
                .foedselsdato(PersonidentifikatorUtil.toBirthdate(fnr))
                .kjoenn(PersonidentifikatorUtil.getKjonn(fnr))
                .rekvireringsstatus(status)
                .rekvirertAv(rekvirertAv)
                .identtype(type)
                .build();
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
}
