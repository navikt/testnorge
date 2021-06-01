package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Organisajon;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Person;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Timeline;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonArbeidsforholdHistorkkService {
    private final ArbeidsforholdSerivce arbeidsforholdSerivce;
    private final ArbeidsforholdHistorikkService arbeidsforholdHistorikkService;
    private final OrganisasjonService organisasjonService;
    private final Executor executor;
    private final Random random = new Random();

    public Flux<Person> generer(Flux<String> identer, String miljo, int months) {
        var organisasjoner = organisasjonService.getOpplysningspliktigeOrganisasjoner(miljo, true);
        return identer.flatMap(ident -> generer(ident, miljo, months, organisasjoner));
    }

    public Flux<Person> generer(Flux<String> identer, String miljo, LocalDate fom, LocalDate tom) {
        var organisasjoner = organisasjonService.getOpplysningspliktigeOrganisasjoner(miljo, false);
        return identer.flatMap(ident -> generer(ident, miljo, fom, tom, organisasjoner));
    }

    private Mono<Person> generer(String ident, String miljo, int months, List<Organisajon> organisajoner) {
        var count = new AtomicInteger();
        return arbeidsforholdSerivce
                .findTimelineFor(ident, miljo)
                .flatMap(timeline -> {
                    var lastDate = timeline.getLastDate();
                    var person = new Person(ident, timeline);
                    var previous = person.getArbeidsforholdOn(lastDate);
                    var map = getArbeidsforholdMap(
                            new ArrayList<>(previous),
                            organisajoner,
                            ident,
                            findAllDatesBetween(lastDate.plusMonths(1), lastDate.plusMonths(months)).iterator()
                    );
                    return updatePerson(count, person, map);
                }).filter(Objects::nonNull);
    }

    private Mono<Person> generer(String ident, String miljo, LocalDate fom, LocalDate tom, List<Organisajon> organisajoner) {
        var count = new AtomicInteger();
        return arbeidsforholdSerivce
                .findTimelineFor(ident, miljo)
                .flatMap(timeline -> {
                    var person = new Person(ident, timeline);
                    var previous = person.getArbeidsforholdOn(fom.minusMonths(1));
                    var map = getArbeidsforholdMap(new ArrayList<>(previous), organisajoner, ident, findAllDatesBetween(fom, tom).iterator());

                    return updatePerson(count, person, map);
                }).filter(Objects::nonNull);
    }

    private Mono<Person> updatePerson(AtomicInteger count, Person person, Mono<Map<LocalDate, List<Arbeidsforhold>>> map) {
        return map.map(value -> {
            count.incrementAndGet();
            if (value == null) {
                return null;
            }
            person.updateTimeline(new Timeline<>(value));
            log.info("Person {} ferdig generert. (Total: {})", person.getIdent(), count.get());
            return person;
        });
    }


    private Mono<Map<LocalDate, List<Arbeidsforhold>>> getArbeidsforholdMap(
            List<Arbeidsforhold> previous,
            List<Organisajon> organisajoner,
            String ident,
            Iterator<LocalDate> dates
    ) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(
                () -> generer(previous, organisajoner, ident, dates),
                executor
        ));
    }


    private Map<LocalDate, List<Arbeidsforhold>> generer(
            List<Arbeidsforhold> previous,
            List<Organisajon> organisajoner,
            String ident,
            Iterator<LocalDate> dates
    ) {
        var map = new HashMap<LocalDate, List<Arbeidsforhold>>();

        if (!dates.hasNext()) {
            return map;
        }

        if (previous == null || previous.isEmpty() || previous.get(0).isForenklet()) {
            var startdato = dates.next();
            var organisajon = organisajoner.get(random.nextInt(organisajoner.size()));
            var arbeidsforhold = arbeidsforholdHistorikkService.genererStart(
                    startdato,
                    organisajon.getRandomVirksomhetsnummer(),
                    organisajon.getOrgnummer(),
                    ident
            ).block();
            map.put(startdato, arbeidsforhold);
            previous = arbeidsforhold;
        }

        if (!dates.hasNext()) {
            return map;
        }

        if (previous.get(0).isForenklet()) {

            var generet = generer(previous, organisajoner, ident, dates);

            if (generet == null) {
                return null;
            }

            map.putAll(generet);
            return map;
        }

        List<LocalDate> dateList = new ArrayList<>();
        dates.forEachRemaining(dateList::add);
        dates = dateList.iterator();

        List<List<Arbeidsforhold>> historikk;
        try {
            historikk = arbeidsforholdHistorikkService.genererHistorikk(
                    previous,
                    dateList.iterator().next(),
                    dateList.size()
            ).block();
        } catch (Exception ex) {
            return null;
        }


        Iterator<Arbeidsforhold> left = historikk.get(0).stream().iterator();
        Iterator<Arbeidsforhold> right = historikk.size() <= 1 ? Collections.emptyIterator() : historikk.get(1).stream().iterator();

        while (dates.hasNext()) {
            var kalendermnd = dates.next();

            boolean isNewArbeidsforhold = previous.get(0).getSluttdato() != null
                    && previous.get(0).getSluttdato().getMonth() == kalendermnd.minusMonths(1).getMonth()
                    && previous.get(0).getSluttdato().getYear() == kalendermnd.minusMonths(1).getYear()
                    || previous.get(0).getSluttdato() != null && previous.get(0).getSluttdato().isBefore(kalendermnd);

            if (isNewArbeidsforhold) {
                map.putAll(generer(null, organisajoner, ident, dates));
                return map;
            }

            var arbeidsforholds = new ArrayList<Arbeidsforhold>();
            arbeidsforholds.add(left.next());
            if (right.hasNext()) {
                arbeidsforholds.add(right.next());
            }

            map.put(kalendermnd, arbeidsforholds);
            previous = arbeidsforholds;
        }
        return map;
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
