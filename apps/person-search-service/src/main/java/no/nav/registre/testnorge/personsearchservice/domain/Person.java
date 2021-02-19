package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.personsearchservice.adapter.model.Navn;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.controller.PersonDTO;

@RequiredArgsConstructor
public class Person {
    private final Response response;

    private Optional<Navn> getNavn() {
        LocalDate now = LocalDate.now();
        Navn navn = response
                .getHentPerson()
                .getNavn()
                .stream()
                .reduce(null, (total, value) -> {
                    if (total == null && (now == value.getGyldigFraOgMed() || now.isAfter(value.getGyldigFraOgMed()))) {
                        total = value;
                    } else if (total != null && (now == value.getGyldigFraOgMed() || now.isAfter(value.getGyldigFraOgMed())) && total.getGyldigFraOgMed().isBefore(value.getGyldigFraOgMed())) {
                        total = value;
                    }
                    return total;
                });
        return Optional.ofNullable(navn);
    }

    public String getFornavn() {
        return getNavn().map(Navn::getFornavn).orElse(null);
    }

    public String getMellomnavn() {
        return getNavn().map(Navn::getMellomnavn).orElse(null);
    }

    public String getEtternavn() {
        return getNavn().map(Navn::getEtternavn).orElse(null);
    }


    public String getIdent() {
        return response
                .getHentIdenter()
                .getIdenter()
                .stream()
                .filter(identer -> identer.getGruppe().equals("FOLKEREGISTERIDENT"))
                .findFirst()
                .orElseThrow()
                .getIdent();
    }

    public String getAktorId() {
        return response
                .getHentIdenter()
                .getIdenter()
                .stream()
                .filter(identer -> identer.getGruppe().equals("AKTORID"))
                .findFirst()
                .orElseThrow()
                .getIdent();
    }

    public List<String> getTags() {
        return response.getTags();
    }


    public PersonDTO toDTO(){
        return PersonDTO
                .builder()
                .fornavn(getFornavn())
                .mellomnavn(getMellomnavn())
                .ettternavn(getEtternavn())
                .aktorId(getAktorId())
                .ident(getIdent())
                .build();
    }
}
