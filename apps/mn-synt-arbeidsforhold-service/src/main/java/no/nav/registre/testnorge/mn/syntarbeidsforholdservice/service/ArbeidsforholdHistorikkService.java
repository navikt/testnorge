package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.consumer.SyntrestConsumer;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.ArbeidsforholdMap;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkService {
    private final Executor executor;
    private final SyntrestConsumer syntrestConsumer;
    private final IdentService identService;
    private final OpplysningspliktigService opplysningspliktigService;
    private final Random random = new Random();


    private Map<LocalDate, Arbeidsforhold> createArbeidsforholdHistorikk(final String ident, Iterator<LocalDate> dates) {
        var map = new HashMap<LocalDate, Arbeidsforhold>();
        var startdato = dates.next();
        log.info("Generer for {} den {}.", ident, startdato);
        var first = syntrestConsumer.getFirstArbeidsforhold(startdato, ident, null);
        map.put(startdato, first);

        if (!dates.hasNext()) {
            return map;
        }

        var previous = first;
        var historikk = syntrestConsumer.getArbeidsforholdHistorikk(previous, startdato).iterator();

        while (dates.hasNext()) {
            LocalDate kalendermaaned = dates.next();
            log.info("Generer for {} den {}.", ident, kalendermaaned);

            boolean isNewArbeidsforhold = previous.getSluttdato() != null
                    && previous.getSluttdato().getMonth() == kalendermaaned.minusMonths(1).getMonth()
                    && previous.getSluttdato().getYear() == kalendermaaned.minusMonths(1).getYear();

            if (previous.getSluttdato() != null) {
                log.info("Sluttdato er satt til {} og neste mnd er {}.",
                        previous.getSluttdato(),
                        kalendermaaned
                );
            }

            if (isNewArbeidsforhold || previous.getSluttdato() != null && previous.getSluttdato().isBefore(kalendermaaned)) {
                log.info("Genrerer nytt arbeidsforhold for {} den {}.", ident, kalendermaaned);
                Arbeidsforhold next = syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, ident, null);
                map.put(kalendermaaned, next);
                previous = next;
            } else {
                if (!historikk.hasNext()) {
                    historikk = syntrestConsumer.getArbeidsforholdHistorikk(previous, kalendermaaned.minusMonths(1)).iterator();
                }
                var next = historikk.next();
                map.put(kalendermaaned, next);
                previous = next;
            }
        }
        log.info("Syntetisk historikk generert for {}.", ident);
        return map;
    }

    private CompletableFuture<ArbeidsforholdMap> futureMap(final String ident, Iterator<LocalDate> dates) {
        return CompletableFuture.supplyAsync(
                () -> createArbeidsforholdHistorikk(ident, dates), executor
        ).thenApply(map -> new ArbeidsforholdMap(ident, map));
    }

    private List<ArbeidsforholdMap> getArbeidsforholdMapList(Set<String> identer, Set<LocalDate> dates) {
        var futures = identer.stream().map(ident -> futureMap(ident, dates.iterator())).collect(Collectors.toList());
        var list = new ArrayList<ArbeidsforholdMap>();
        for (var future : futures) {
            try {
                list.add(future.get());
            } catch (Exception e) {
                log.error("Feil med generering av syntetsik historikk. Forsetter uten.", e);
            }
        }
        if (futures.size() > list.size()) {
            log.warn("Syntentiserer bare {}/{} personer pga tidligere feil.", list.size(), futures.size());
        }
        return list;
    }

    public void reportAll(LocalDate fom, LocalDate tom, int maxIdenter, String miljo) {

        var startTimeStamp = LocalDateTime.now();

        log.info("Starter syntentisering ({}).", startTimeStamp);
        var dates = findAllDatesBetween(fom, tom);
        var identer = identService.getIdenterUtenArbeidsforhold(miljo, maxIdenter);
        if (identer.isEmpty()) {
            log.warn("Fant ingen identer avsluter syntentisering...");
        }

        log.info("Syntentiser for {} person(er) mellom {} - {}...", identer.size(), fom, tom);
        var arbeidsforholdMapList = getArbeidsforholdMapList(identer, dates);

        for (var kalenermnd : dates) {
            report(arbeidsforholdMapList, kalenermnd, miljo);
        }
        log.info("Syntentisering ferdig for {}/{} person(er) ({} - {}).",
                arbeidsforholdMapList.size(),
                identer.size(),
                startTimeStamp,
                LocalDateTime.now()
        );
    }

    private void report(List<ArbeidsforholdMap> arbeidsforholdMapList, LocalDate kalenermnd, String miljo) {
        var opplysningspliktige = opplysningspliktigService.getAllOpplysningspliktig(kalenermnd, miljo);
        for (var arbeidsforholdMap : arbeidsforholdMapList) {
            var arbeidsforhold = arbeidsforholdMap.getArbeidsforhold(kalenermnd);
            log.info("Legger til arbeidsforhold for {} den {}.", arbeidsforhold.getIdent(), kalenermnd);
            if (arbeidsforholdMap.isNewArbeidsforhold(kalenermnd)) {
                var opplysningspliktig = opplysningspliktige.get(random.nextInt(opplysningspliktige.size()));
                arbeidsforhold.setVirksomhetsnummer(opplysningspliktig.getRandomVirksomhetsnummer());
                log.info("{} starter nytt arbeidsfohold den {}.", arbeidsforhold.getIdent(), kalenermnd);
            } else {
                var virksomhetsnummer = arbeidsforholdMap.getArbeidsforhold(kalenermnd.minusMonths(1)).getVirksomhetsnummer();
                if (virksomhetsnummer == null) {
                    throw new RuntimeException("Finner ikke forige virksomhentsnummer");
                }
                arbeidsforhold.setVirksomhetsnummer(virksomhetsnummer);
            }
            var opplysningspliktig = findOpplysningspliktigForVirksomhent(arbeidsforhold.getVirksomhetsnummer(), opplysningspliktige);
            opplysningspliktig.addArbeidsforhold(arbeidsforhold);
        }
        opplysningspliktigService.send(opplysningspliktige, miljo);
    }


    private Opplysningspliktig findOpplysningspliktigForVirksomhent(String virksomhentsnummer, List<Opplysningspliktig> list) {
        return list.stream()
                .filter(value -> value.driverVirksomhent(virksomhentsnummer))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finner ikke opplysningspliktig for virksomhentsnummer: " + virksomhentsnummer));
    }


    private Set<LocalDate> findAllDatesBetween(LocalDate fom, LocalDate tom) {
        Set<LocalDate> dates = new TreeSet<>();
        if (tom == null) {
            return dates;
        }
        Period between = Period.between(
                fom.withDayOfMonth(1),
                tom.withDayOfMonth(1)
        );
        int months = between.getYears() * 12 + between.getMonths();
        for (int index = 0; index <= months; index++) {
            dates.add(fom.withDayOfMonth(1).plusMonths(index));
        }
        return dates;
    }

}