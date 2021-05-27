package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.OppsummeringsdokumentTimeline;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Person;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Oppsummeringsdokument;

@Slf4j
@Service
@RequiredArgsConstructor
public class OppsummeringsdokumentService {

    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;

    public void save(Flux<Person> personer, String miljo) {
        var list = personer.collectList().block();
        save(list, miljo);
    }

    public void save(List<Person> personer, String miljo) {
        log.info(
                "Legger til arbeidsforhold for {}.",
                personer.stream().map(Person::getIdent).collect(Collectors.joining(", "))
        );

        var dates = personer
                .stream()
                .map(value -> value.getTimeline().getUpdatedDates())
                .reduce(new HashSet<>(), (sub, item) -> {
                    sub.addAll(item);
                    return sub;
                });

        dates.parallelStream()
                .flatMap(kalendermnd -> getOppdatertOppsumeringsdokument(personer, kalendermnd, miljo).stream())
                .collect(Collectors.groupingBy(Oppsummeringsdokument::getId))
                .values()
                .parallelStream()
                .map(OppsummeringsdokumentTimeline::new)
                .forEach(timeline -> timeline.applyForAll((value) -> oppsummeringsdokumentConsumer.save(value.toDTO(), miljo).block()));
    }

    private List<Oppsummeringsdokument> getOppdatertOppsumeringsdokument(List<Person> personer, LocalDate kalendermnd, String miljo) {
        log.info("Oppdaterer arbeidsforhold på opplysningspliktig...");
        var oppsummeringsdokumenter = Flux.concat(
                personer.stream()
                        .flatMap(person -> person.getArbeidsforholdOn(kalendermnd).stream())
                        .collect(Collectors.groupingBy(Arbeidsforhold::getOpplysningspliktig))
                        .entrySet()
                        .stream()
                        .map(entry -> oppsummeringsdokumentConsumer.getOppsummeringsdokument(entry.getKey(), kalendermnd, miljo)
                                .map(oppsummeringsdokument -> {
                                    oppsummeringsdokument.addAll(entry.getValue());
                                    return oppsummeringsdokument;
                                })
                        ).collect(Collectors.toList())
        ).collectList().block();
        log.info("Oppdatert {} opplysningsplikitg med nye arbeidsforhold.", oppsummeringsdokumenter.size());
        return oppsummeringsdokumenter;
    }
}
