package no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;

import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.consumer.OppsummeringsdokumentConsumer;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.Person;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Arbeidsforhold;
import no.nav.registre.testnav.genererarbeidsforholdpopulasjonservice.domain.amelding.Oppsummeringsdokument;

@Slf4j
@Service
@RequiredArgsConstructor
public class OppsummeringsdokumentService {

    private final OppsummeringsdokumentConsumer oppsummeringsdokumentConsumer;

    public void save(Flux<Person> personer, String miljo, LocalDate fom, LocalDate tom) {
        save(personer.collectList().block(), miljo, fom, tom);
    }

    public void save(List<Person> personer, String miljo, LocalDate fom, LocalDate tom) {
        var dokumenter = findAllDatesBetween(fom, tom).stream()
                .flatMap(kalendermnd -> getOppdatertOppsumeringsdokument(personer, kalendermnd, miljo).stream())
                .map(Oppsummeringsdokument::toDTO)
                .collect(Collectors.toList());
        log.info("Lager {} oppsummeringsdokumentene i {}...", dokumenter.size(), miljo);
        oppsummeringsdokumentConsumer.saveAll(dokumenter, miljo);
        log.info("Oppsummeringsdokumentene {} lagret i {}", dokumenter.size(), miljo);
    }

    private List<Oppsummeringsdokument> getOppdatertOppsumeringsdokument(List<Person> personer, LocalDate kalendermnd, String miljo) {
        log.info("Finner arbeidsforhold som skal fjernes fra opplysningsplkiktige...");
        var oppsummeringsdokumentWithRemovedArbeidsforhold = Flux.concat(personer.stream()
                .flatMap(person -> person.getArbeidsforholdToRemoveOn(kalendermnd).stream())
                .collect(Collectors.groupingBy(Arbeidsforhold::getOpplysningspliktig))
                .entrySet()
                .stream()
                .map(entry -> oppsummeringsdokumentConsumer.getOppsummeringsdokument(entry.getKey(), kalendermnd, miljo))
                .collect(Collectors.toList())
        ).collectList().block();
        log.info("Fant {} arbeidsforhold som skal fjernes fra opplysningsplikitge.", oppsummeringsdokumentWithRemovedArbeidsforhold.size());

        log.info("Oppdaterer arbeidsforhold på opplysningspliktig...");
        var oppsummeringsdokumenter = Flux.concat(
                personer.stream()
                        .flatMap(person -> person.getArbeidsforholdOn(kalendermnd).stream())
                        .collect(Collectors.groupingBy(Arbeidsforhold::getOpplysningspliktig))
                        .entrySet()
                        .stream()
                        .map(entry -> find(oppsummeringsdokumentWithRemovedArbeidsforhold, entry.getKey(), kalendermnd, miljo).map(oppsummeringsdokument -> {
                                    oppsummeringsdokument.addAll(entry.getValue());
                                    return oppsummeringsdokument;
                                })
                        ).collect(Collectors.toList())
        ).collectList().block();

        log.info("Oppdatert {} opplysningsplikitg med nye arbeidsforhold.", oppsummeringsdokumenter.size());

        for (var oppsummeringsdokument : oppsummeringsdokumentWithRemovedArbeidsforhold) {
            var empty = oppsummeringsdokumenter.stream()
                    .filter(value -> value.getOpplysningspliktigOrganisajonsnummer().equals(oppsummeringsdokument.getOpplysningspliktigOrganisajonsnummer()))
                    .findAny()
                    .isEmpty();

            if (empty) {
                oppsummeringsdokumenter.add(oppsummeringsdokument);
            }
        }
        return oppsummeringsdokumenter;
    }

    private Mono<Oppsummeringsdokument> find(List<Oppsummeringsdokument> list, String opplysningspliktigOrgnummer, LocalDate kalendermnd, String miljo) {
        return list.stream()
                .filter(value -> value.getOpplysningspliktigOrganisajonsnummer().equals(opplysningspliktigOrgnummer))
                .findFirst()
                .map(Mono::just)
                .orElseGet(() -> oppsummeringsdokumentConsumer.getOppsummeringsdokument(opplysningspliktigOrgnummer, kalendermnd, miljo));
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
