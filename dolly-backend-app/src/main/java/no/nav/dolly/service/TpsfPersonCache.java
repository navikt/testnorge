package no.nav.dolly.service;

import static java.util.Collections.singletonList;
import static java.util.Objects.isNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.resultset.tpsf.Person;
import no.nav.dolly.domain.resultset.tpsf.Relasjon;
import no.nav.dolly.domain.resultset.tpsf.RsOppdaterPersonResponse;
import no.nav.dolly.domain.resultset.tpsf.TpsPerson;

@Service
@RequiredArgsConstructor
public class TpsfPersonCache {

    private final TpsfService tpsfService;

    public TpsPerson fetchIfEmpty(TpsPerson tpsPerson) {

        List<String> tpsfIdenter = new ArrayList<>();
        Stream.of(singletonList(tpsPerson.getHovedperson()), tpsPerson.getPartnere(), tpsPerson.getBarn()).forEach(tpsfIdenter::addAll);

        AtomicBoolean notFound = new AtomicBoolean(false);
        tpsfIdenter.forEach(ident -> {
            if (isNull(tpsPerson.getPerson(ident))) {
                notFound.set(true);
            }
        });
        if (notFound.get()) {
            tpsPerson.setPersondetaljer(tpsfService.hentTestpersoner(tpsfIdenter));
        }

        return tpsPerson;
    }

    public TpsPerson prepareTpsPersoner(RsOppdaterPersonResponse identer) {

        List<Person> personer = tpsfService.hentTestpersoner(singletonList(identer.getIdentTupler().get(0).getIdent()));

        if (!personer.isEmpty()) {
            return TpsPerson.builder()
                    .persondetaljer(personer)
                    .hovedperson(personer.get(0).getIdent())
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
                .build());
    }
}
