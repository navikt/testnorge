package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.adapter.ArbeidsforholdHistorikkAdapter;
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
    private final ArbeidsforholdHistorikkAdapter historikkAdapter;


    private Map<LocalDate, Arbeidsforhold> createArbeidsforholdHistorikk(final String ident, Iterator<LocalDate> dates) {
        var map = new HashMap<LocalDate, Arbeidsforhold>();
        var startdato = dates.next();
        log.info("Generer for {} den {}.", ident, startdato);
        var first = syntrestConsumer.getFirstArbeidsforhold(startdato, ident, null);
        map.put(startdato, first);

        if (!dates.hasNext()) {
            return map;
        }

        if (first.isForenklet()) {
            map.putAll(createArbeidsforholdHistorikk(ident, dates));
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
                log.info("Genererer nytt arbeidsforhold for {} den {}.", ident, kalendermaaned);
                Arbeidsforhold next = syntrestConsumer.getFirstArbeidsforhold(kalendermaaned, ident, null);
                historikk = Collections.emptyIterator();
                map.put(kalendermaaned, next);
                previous = next;
            } else {
                if (!historikk.hasNext()) {
                    historikk = syntrestConsumer.getArbeidsforholdHistorikk(previous, kalendermaaned.minusMonths(1)).iterator();
                    if (!historikk.hasNext()) {
                        log.warn("Forsetter ikke pa arbeidsforhold da historikk ikke finnes for {}.", ident);
                        return map;
                    }
                }
                var next = historikk.next();
                map.put(kalendermaaned, next);
                previous = next;
            }
        }
        log.info("Syntetisk historikk generert for {}.", ident);
        return map;
    }

    private CompletableFuture<ArbeidsforholdMap> futureMap(final String ident, Set<LocalDate> dates) {
        return CompletableFuture.supplyAsync(
                () -> createArbeidsforholdHistorikk(ident, dates.iterator()), executor
        ).thenApply(map -> new ArbeidsforholdMap(ident, map));
    }

    private List<ArbeidsforholdMap> getArbeidsforholdMapList(Set<String> identer, Set<LocalDate> dates) {
        var futures = identer.stream().map(ident -> futureMap(ident, dates)).collect(Collectors.toList());
        var list = new ArrayList<ArbeidsforholdMap>();
        for (var future : futures) {
            try {
                var map = future.get(10, TimeUnit.MINUTES);
                list.add(map);
            } catch (Exception e) {
                log.error("Feil ved generering av syntetisk historikk. Fortsetter uten.", e);
            }
        }
        if (list.isEmpty()) {
            log.warn("Fikk ikke syntentisert {} identer pga tidligere feil.", futures.size());
        } else if (futures.size() > list.size()) {
            log.warn("Syntetiserer bare {}/{} personer pga tidligere feil.", list.size(), futures.size());
        }
        return list;
    }

    public List<String> reportAll(LocalDate fom, LocalDate tom, int maxIdenter, String miljo) {

        var startTimeStamp = LocalDateTime.now();

        log.info("Starter syntetisering ({}).", startTimeStamp);
        var dates = findAllDatesBetween(fom, tom);
        var identer = identService.getIdenterUtenArbeidsforhold(miljo, maxIdenter);
        if (identer.isEmpty()) {
            log.warn("Fant ingen identer. Avslutter syntetisering...");
            return Collections.emptyList();
        }

        log.info("Syntentiser for {} person(er) mellom {} - {}...", identer.size(), fom, tom);
        var arbeidsforholdMapList = getArbeidsforholdMapList(identer, dates);

        if (arbeidsforholdMapList.isEmpty()) {
            log.warn("Fikk ikke opprettet syntetisk arbeidsforhold. Avslutter syntetisering...");
            return Collections.emptyList();
        }

        for (var kalenermnd : dates) {
            report(arbeidsforholdMapList, kalenermnd, miljo);
        }
        log.info("Syntetisering ferdig for {}/{} person(er) ({} - {}).",
                arbeidsforholdMapList.size(),
                identer.size(),
                startTimeStamp,
                LocalDateTime.now()
        );
        return arbeidsforholdMapList.stream().map(ArbeidsforholdMap::getIdent).collect(Collectors.toList());
    }

    private void report(List<ArbeidsforholdMap> arbeidsforholdMapList, LocalDate kalenermnd, String miljo) {
        var opplysningspliktige = opplysningspliktigService.getAllOpplysningspliktig(kalenermnd, miljo);
        for (var arbeidsforholdMap : arbeidsforholdMapList) {
            var arbeidsforhold = arbeidsforholdMap.getArbeidsforhold(kalenermnd);
            log.info("Legger til arbeidsforhold for {} den {}.", arbeidsforhold.getIdent(), kalenermnd);
            if (arbeidsforholdMap.isNewArbeidsforhold(kalenermnd)
                    || arbeidsforhold.isForenklet() && !arbeidsforholdMap.contains(kalenermnd.minusMonths(1))) {
                var opplysningspliktig = opplysningspliktige.get(random.nextInt(opplysningspliktige.size()));
                arbeidsforhold.setVirksomhetsnummer(opplysningspliktig.getRandomVirksomhetsnummer());
                log.info("{} starter nytt arbeidsforhold den {}.", arbeidsforhold.getIdent(), kalenermnd);
            } else {
                var virksomhetsnummer = arbeidsforholdMap.getArbeidsforhold(kalenermnd.minusMonths(1)).getVirksomhetsnummer();
                if (virksomhetsnummer == null) {
                    throw new RuntimeException("Finner ikke forrige virksomhetsnummer");
                }
                arbeidsforhold.setVirksomhetsnummer(virksomhetsnummer);
            }
            var opplysningspliktig = findOpplysningspliktigForVirksomhet(arbeidsforhold.getVirksomhetsnummer(), opplysningspliktige);
            opplysningspliktig.addArbeidsforhold(arbeidsforhold);
            historikkAdapter.save(arbeidsforhold.toHistorikk(miljo));
        }
        opplysningspliktigService.send(opplysningspliktige, miljo);
    }


    private Opplysningspliktig findOpplysningspliktigForVirksomhet(String virksomhetsnummer, List<Opplysningspliktig> list) {
        return list.stream()
                .filter(value -> value.driverVirksomhet(virksomhetsnummer))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Finner ikke opplysningspliktig for virksomhetsnummer: " + virksomhetsnummer));
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