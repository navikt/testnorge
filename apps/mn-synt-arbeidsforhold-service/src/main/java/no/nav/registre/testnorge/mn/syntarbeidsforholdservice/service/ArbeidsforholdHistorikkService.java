package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Arbeidsforhold;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.ArbeidsforholdMap;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Opplysningspliktig;
import no.nav.registre.testnorge.mn.syntarbeidsforholdservice.domain.Organisajon;

@Slf4j
@Service
@RequiredArgsConstructor
public class ArbeidsforholdHistorikkService {
    private final Executor executor;
    private final IdentService identService;
    private final OpplysningspliktigService opplysningspliktigService;
    private final ArbeidsforholdSyntService arbeidsforholdSyntService;

    private Map<LocalDate, List<Arbeidsforhold>> developArbeidsforhold(Set<LocalDate> dates, String miljo) {
        var map = new HashMap<LocalDate, List<Arbeidsforhold>>();
        var kalenermnd = dates.stream().findFirst();

        if (kalenermnd.isEmpty()) {
            log.warn("Kalenermnd finnes ikke. Stopper genrering.");
            return map;
        }
        var organisasjoner = opplysningspliktigService.getOpplysningspliktigeOrganisasjoner(miljo);
        var opplysningspliktigList = opplysningspliktigService.getAllOpplysningspliktig(kalenermnd.get().minusMonths(1), miljo);

        opplysningspliktigList.forEach(opplysningspliktig -> {
            var futures = opplysningspliktig.getArbeidsforhold()
                    .stream()
                    .map(arbeidsforhold -> futureMap(dates.iterator(), arbeidsforhold, organisasjoner))
                    .collect(Collectors.toList());

            for (var future : futures) {
                try {
                    future.get().forEach((key, value) -> {
                        if (map.containsKey(key)) {
                            map.get(key).add(value);
                        } else {
                            var list = new ArrayList<Arbeidsforhold>();
                            list.add(value);
                            map.put(key, list);
                        }
                    });
                } catch (Exception e) {
                    log.error("Feil med utviukling av populasjon.", e);
                }
            }
        });
        return map;
    }

    private Map<LocalDate, Arbeidsforhold> develop(Iterator<LocalDate> dates, Arbeidsforhold previous, List<Organisajon> organisajoner) {
        var map = new HashMap<LocalDate, Arbeidsforhold>();
        Iterator<Arbeidsforhold> historikk = Collections.emptyIterator();

        if (!dates.hasNext()) {
            return map;
        }

        while (dates.hasNext()) {
            LocalDate kalendermaaned = dates.next();
            log.info("Generer for {} den {}.", previous.getIdent(), kalendermaaned);

            boolean isNewArbeidsforhold = previous.getSluttdato() != null
                    && previous.getSluttdato().getMonth() == kalendermaaned.minusMonths(1).getMonth()
                    && previous.getSluttdato().getYear() == kalendermaaned.minusMonths(1).getYear();

            if (previous.getSluttdato() != null) {
                log.info("Sluttdato er satt til {} og neste mnd er {}.",
                        previous.getSluttdato(),
                        kalendermaaned
                );
            }

            if (isNewArbeidsforhold || previous.getSluttdato() != null && previous.getSluttdato().isBefore(kalendermaaned) || previous.isForenklet()) {
                log.info("Genererer nytt arbeidsforhold for {} den {}.", previous.getIdent(), kalendermaaned);
                Arbeidsforhold next = arbeidsforholdSyntService.getFirstArbeidsforhold(organisajoner, kalendermaaned, previous.getIdent());
                historikk = Collections.emptyIterator();
                map.put(kalendermaaned, next);
                previous = next;
            } else {
                if (!historikk.hasNext()) {
                    historikk = arbeidsforholdSyntService.getArbeidsforholdHistorikk(previous, kalendermaaned.minusMonths(1), null).iterator();
                    if (!historikk.hasNext()) {
                        log.warn("Forsetter ikke pa arbeidsforhold da historikk ikke finnes for {}.", previous.getIdent());
                        return map;
                    }
                }
                var next = historikk.next();
                map.put(kalendermaaned, next);
                previous = next;
            }
        }
        log.info("Syntetisk historikk generert for {}.", previous.getIdent());
        return map;
    }

    private Map<LocalDate, Arbeidsforhold> createArbeidsforholdHistorikk(final String ident, Iterator<LocalDate> dates, List<Organisajon> organisajoner) {
        var map = new HashMap<LocalDate, Arbeidsforhold>();
        var startdato = dates.next();
        log.info("Generer for {} den {}.", ident, startdato);
        var first = arbeidsforholdSyntService.getFirstArbeidsforhold(organisajoner, startdato, ident);
        map.put(startdato, first);

        if (!dates.hasNext()) {
            return map;
        }

        if (first.isForenklet()) {
            map.putAll(createArbeidsforholdHistorikk(ident, dates, organisajoner));
            return map;
        }


        var previous = first;
        var historikk = arbeidsforholdSyntService.getArbeidsforholdHistorikk(previous, startdato, null).iterator();

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
                Arbeidsforhold next = arbeidsforholdSyntService.getFirstArbeidsforhold(organisajoner, kalendermaaned, ident);
                historikk = Collections.emptyIterator();
                map.put(kalendermaaned, next);
                previous = next;
            } else {
                if (!historikk.hasNext()) {
                    historikk = arbeidsforholdSyntService.getArbeidsforholdHistorikk(previous, kalendermaaned.minusMonths(1), null).iterator();
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


    private CompletableFuture<Map<LocalDate, Arbeidsforhold>> futureMap(Iterator<LocalDate> dates, Arbeidsforhold previous, List<Organisajon> organisajoner) {
        return CompletableFuture.supplyAsync(() -> develop(dates, previous, organisajoner), executor);
    }

    private CompletableFuture<ArbeidsforholdMap> futureMap(final String ident, Set<LocalDate> dates, List<Organisajon> organisajoner) {
        return CompletableFuture.supplyAsync(
                () -> createArbeidsforholdHistorikk(ident, dates.iterator(), organisajoner), executor
        ).thenApply(map -> new ArbeidsforholdMap(ident, map));
    }

    private List<ArbeidsforholdMap> getArbeidsforholdMapList(Flux<String> identer, Set<LocalDate> dates, String miljo) {
        var organisasjoner = opplysningspliktigService.getOpplysningspliktigeOrganisasjoner(miljo);

        var futures = identer.map(ident -> futureMap(ident, dates, organisasjoner)).collectList().block();

        var list = new ArrayList<ArbeidsforholdMap>();

        for (int index = 0; index < futures.size(); index++) {
            try {
                var map = futures.get(index).get(12, TimeUnit.MINUTES);
                log.info("{}/{} arbeidsforhold historikk generert.", index + 1, futures.size());
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

    public List<String> populate(LocalDate fom, LocalDate tom, int maxIdenter, String miljo) {
        var startTimeStamp = LocalDateTime.now();
        log.info("Starter syntetisering ({}).", startTimeStamp);

        var dates = findAllDatesBetween(fom, tom);
        var identer = identService.getIdenterUtenArbeidsforhold(miljo, maxIdenter);

        log.info("Syntentiser for person(er) mellom {} - {}...", fom, tom);
        var arbeidsforholdMapList = getArbeidsforholdMapList(identer, dates, miljo);

        if (arbeidsforholdMapList.isEmpty()) {
            log.warn("Fikk ikke opprettet syntetisk arbeidsforhold. Avslutter syntetisering...");
            return Collections.emptyList();
        }

        for (var kalenermnd : dates) {
            report(arbeidsforholdMapList, kalenermnd, miljo);
        }
        log.info("Syntetisering ferdig for {} person(er) ({} - {}).",
                arbeidsforholdMapList.size(),
                startTimeStamp,
                LocalDateTime.now()
        );
        return arbeidsforholdMapList.stream().map(ArbeidsforholdMap::getIdent).collect(Collectors.toList());
    }

    public void develop(LocalDate fom, LocalDate tom, String miljo) {
        var startTimeStamp = LocalDateTime.now();
        log.info("Starter utvikling av populasjon ({}).", startTimeStamp);

        developArbeidsforhold(findAllDatesBetween(fom, tom), miljo).forEach((kalenermnd, list) -> {
            var opplysningspliktigList = opplysningspliktigService.getAllOpplysningspliktig(kalenermnd, miljo);
            list.forEach(arbeidsforhold -> {
                var opplysningspliktig = findOpplysningspliktigForVirksomhet(arbeidsforhold.getVirksomhetsnummer(), opplysningspliktigList);
                opplysningspliktig.addArbeidsforhold(arbeidsforhold);
            });
            opplysningspliktigService.send(opplysningspliktigList, miljo);
        });

        log.info("Utvikling av populasjon ferdig ({} - {}).",
                startTimeStamp,
                LocalDateTime.now()
        );
    }

    private void report(List<ArbeidsforholdMap> arbeidsforholdMapList, LocalDate kalenermnd, String miljo) {
        var opplysningspliktige = opplysningspliktigService.getAllOpplysningspliktig(kalenermnd, miljo);
        for (var arbeidsforholdMap : arbeidsforholdMapList) {
            var arbeidsforhold = arbeidsforholdMap.getArbeidsforhold(kalenermnd);
            log.trace("Legger til arbeidsforhold for {} den {}.", arbeidsforhold.getIdent(), kalenermnd);

            var opplysningspliktig = findOpplysningspliktigForVirksomhet(arbeidsforhold.getVirksomhetsnummer(), opplysningspliktige);
            opplysningspliktig.addArbeidsforhold(arbeidsforhold);
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


