package no.nav.dolly.service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsFullmakt;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.RsSimplePerson;
import no.nav.dolly.domain.resultset.tpsf.RsVergemaal;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;
import no.nav.dolly.domain.resultset.tpsf.adresse.IdentHistorikk;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;

@Service
@RequiredArgsConstructor
public class TpsfPersonCache {

    private final TpsfService tpsfService;

    public TpsPerson fetchIfEmpty(TpsPerson tpsPerson) {

        Set<String> tpsfIdenter = new HashSet<>();
        Stream.of(singletonList(tpsPerson.getHovedperson()), tpsPerson.getPartnere(),
                tpsPerson.getBarn(), tpsPerson.getVerger(), tpsPerson.getIdenthistorikk())
                .forEach(tpsfIdenter::addAll);

        List<String> manglendeIdenter = tpsfIdenter.stream().filter(ident -> tpsPerson.getPersondetaljer().stream()
                .noneMatch(person -> person.getIdent().equals(ident)))
                .collect(Collectors.toList());

        if (!manglendeIdenter.isEmpty()) {
            tpsPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(manglendeIdenter));
        }

        List<String> historikkIdenter = tpsPerson.getPerson(tpsPerson.getHovedperson()).getIdentHistorikk().stream()
                .map(IdentHistorikk::getAliasPerson)
                .filter(person -> tpsPerson.getPersondetaljer().stream()
                        .noneMatch(person1 -> person.getIdent().equals(person1.getIdent())))
                .map(Person::getIdent)
                .collect(Collectors.toList());

        if (!historikkIdenter.isEmpty()) {
            tpsPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(historikkIdenter));
        }

        List<String> vergeIdenter = tpsPerson.getPersondetaljer().stream()
                .filter(person -> !person.getVergemaal().isEmpty() && person.getVergemaal().stream()
                        .noneMatch(vergemaal -> tpsPerson.getPersondetaljer().stream()
                                .anyMatch(person1 -> vergemaal.getVerge().getIdent().equals(person1.getIdent()))))
                .map(Person::getVergemaal)
                .flatMap(vergemaal -> vergemaal.stream().map(RsVergemaal::getVerge))
                .map(RsSimplePerson::getIdent)
                .collect(Collectors.toList());

        if (!vergeIdenter.isEmpty()) {
            tpsPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(vergeIdenter));
        }

        List<String> fullmaktIdenter = tpsPerson.getPersondetaljer().stream()
                .filter(person -> !person.getFullmakt().isEmpty() && person.getFullmakt().stream()
                        .noneMatch(fullmakt -> tpsPerson.getPersondetaljer().stream()
                                .anyMatch(person1 -> fullmakt.getFullmektig().getIdent().equals(person1.getIdent()))))
                .map(Person::getFullmakt)
                .flatMap(fullmakt -> fullmakt.stream().map(RsFullmakt::getFullmektig))
                .map(RsSimplePerson::getIdent)
                .collect(Collectors.toList());

        if (!fullmaktIdenter.isEmpty()) {
            tpsPerson.getPersondetaljer().addAll(tpsfService.hentTestpersoner(fullmaktIdenter));
        }

        return tpsPerson;
    }

    public TpsPerson prepareTpsPersoner(RsOppdaterPersonResponse identer) {

        List<Person> personer = tpsfService.hentTestpersoner(identer.getIdentTupler().stream()
                .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                .collect(Collectors.toList()));

        if (!personer.isEmpty()) {
            return TpsPerson.builder()
                    .persondetaljer(personer)
                    .hovedperson(identer.getIdentTupler().get(0).getIdent())
                    .partnere(personer.get(0).getRelasjoner().stream()
                            .filter(Relasjon::isPartner)
                            .map(Relasjon::getPersonRelasjonMed)
                            .map(Person::getIdent)
                            .collect(Collectors.toList()))
                    .barn(personer.get(0).getRelasjoner().stream()
                            .filter(Relasjon::isBarn)
                            .map(Relasjon::getPersonRelasjonMed)
                            .map(Person::getIdent)
                            .collect(Collectors.toList()))
                    .nyePartnereOgBarn(identer.getIdentTupler().stream()
                            .filter(RsOppdaterPersonResponse.IdentTuple::isLagtTil)
                            .map(RsOppdaterPersonResponse.IdentTuple::getIdent)
                            .collect(Collectors.toList()))
                    .verger(personer.get(0).getVergemaal().stream()
                            .map(RsVergemaal::getVerge)
                            .map(RsSimplePerson::getIdent)
                            .collect(Collectors.toList()))
                    .fullmektige(personer.get(0).getFullmakt().stream()
                            .map(RsFullmakt::getFullmektig)
                            .map(RsSimplePerson::getIdent)
                            .collect(Collectors.toList()))
                    .identhistorikk(personer.get(0).getIdentHistorikk().stream()
                            .map(IdentHistorikk::getAliasPerson)
                            .map(Person::getIdent)
                            .collect(Collectors.toList()))
                    .build();
        }

        return new TpsPerson();
    }

    public TpsPerson prepareTpsPersoner(Person person) {

        return fetchIfEmpty(TpsPerson.builder()
                .hovedperson(person.getIdent())
                .partnere(person.getRelasjoner().stream()
                        .filter(Relasjon::isPartner)
                        .map(Relasjon::getPersonRelasjonMed)
                        .map(Person::getIdent)
                        .collect(Collectors.toList()))
                .barn(person.getRelasjoner().stream()
                        .filter(Relasjon::isBarn)
                        .map(Relasjon::getPersonRelasjonMed)
                        .map(Person::getIdent)
                        .collect(Collectors.toList()))
                .verger(person.getVergemaal().stream()
                        .map(RsVergemaal::getVerge)
                        .map(RsSimplePerson::getIdent)
                        .collect(Collectors.toList()))
                .fullmektige(person.getFullmakt().stream()
                        .map(RsFullmakt::getFullmektig)
                        .map(RsSimplePerson::getIdent)
                        .collect(Collectors.toList()))
                .identhistorikk(person.getIdentHistorikk().stream()
                        .map(IdentHistorikk::getAliasPerson)
                        .map(Person::getIdent)
                        .collect(Collectors.toList()))
                .build());
    }
}
