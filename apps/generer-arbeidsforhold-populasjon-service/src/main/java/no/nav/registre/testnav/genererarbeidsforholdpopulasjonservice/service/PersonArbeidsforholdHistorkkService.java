package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
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


    public Flux<Person> generer(Flux<String> identer, String miljo, LocalDate fom, LocalDate tom) {
        var organisasjoner = organisasjonService.getOpplysningspliktigeOrganisasjoner(miljo);
        return identer.flatMap(ident -> generer(ident, miljo, fom, tom, organisasjoner));
    }

    private Mono<Person> generer(String ident, String miljo, LocalDate fom, LocalDate tom, List<Organisajon> organisajoner) {
        return arbeidsforholdSerivce
                .findTimelineFor(ident, miljo)
                .flatMap(timeline -> {
                    var person = new Person(ident, timeline);
                    var previous = person.getArbeidsforholdOn(fom.minusMonths(1)).stream().findFirst().orElse(null);
                    var map = getArbeidsforholdMap(previous, organisajoner, ident, findAllDatesBetween(fom, tom).iterator());
                    return map.map(value -> {
                        person.updateTimeline(new Timeline<>(value));
                        log.info("Person {} ferdig generert.", person.getIdent());
                        return person;
                    });
                });
    }


    private Mono<Map<LocalDate, Arbeidsforhold>> getArbeidsforholdMap(
            Arbeidsforhold previous,
            List<Organisajon> organisajoner,
            String ident,
            Iterator<LocalDate> dates
    ) {
        return Mono.fromFuture(CompletableFuture.supplyAsync(
                () -> generer(previous, organisajoner, ident, dates),
                executor
        ));
    }


    private Map<LocalDate, Arbeidsforhold> generer(
            Arbeidsforhold previous,
            List<Organisajon> organisajoner,
            String ident,
            Iterator<LocalDate> dates
    ) {
        var map = new HashMap<LocalDate, Arbeidsforhold>();

        if (previous == null || previous.isForenklet()) {
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

        if (previous.isForenklet()) {
            map.putAll(generer(previous, organisajoner, ident, dates));
            return map;
        }

        List<LocalDate> dateList = new ArrayList<>();
        dates.forEachRemaining(dateList::add);
        dates = dateList.iterator();
        var kalendermnd = dates.next();

        var historikk = arbeidsforholdHistorikkService.genererHistorikk(previous, kalendermnd, dateList.size()).block().iterator();

        while (dates.hasNext()) {
            var arbeidsforhold = historikk.next();

            boolean isNewArbeidsforhold = previous.getSluttdato() != null
                    && previous.getSluttdato().getMonth() == kalendermnd.minusMonths(1).getMonth()
                    && previous.getSluttdato().getYear() == kalendermnd.minusMonths(1).getYear()
                    || previous.getSluttdato() != null && previous.getSluttdato().isBefore(kalendermnd);


            if (isNewArbeidsforhold) {
                map.putAll(generer(null, organisajoner, ident, dates));
                return map;
            }

            map.put(kalendermnd, arbeidsforhold);
            previous = arbeidsforhold;
            kalendermnd = dates.next();
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
