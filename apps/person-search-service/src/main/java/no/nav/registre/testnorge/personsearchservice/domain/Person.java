package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.personsearchservice.adapter.model.Kjoenn;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Navn;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.controller.PersonDTO;

@RequiredArgsConstructor
public class Person {
    private final Response response;

    private Optional<Navn> getNavn() {
        return response
                .getHentPerson()
                .getNavn()
                .stream()
                .filter(value -> !value.getMetadata().getHistorisk())
                .findFirst();
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

    public String getKjoenn() {
        return response
                .getHentPerson()
                .getKjoenn()
                .stream()
                .filter(value -> !value.getMetadata().getHistorisk())
                .findFirst()
                .map(Kjoenn::getKjoenn)
                .orElse(null);
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


    public PersonDTO toDTO() {
        return PersonDTO
                .builder()
                .fornavn(getFornavn())
                .mellomnavn(getMellomnavn())
                .ettternavn(getEtternavn())
                .aktorId(getAktorId())
                .ident(getIdent())
                .tag(getTags().stream().findFirst().orElse(null))
                .build();
    }
}
