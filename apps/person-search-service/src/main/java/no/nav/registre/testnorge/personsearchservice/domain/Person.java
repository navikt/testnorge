package no.nav.registre.testnorge.personsearchservice.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.personsearchservice.adapter.model.Foedsel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Kjoenn;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Navn;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Sivilstand;
import no.nav.registre.testnorge.personsearchservice.adapter.model.WithMetadata;
import no.nav.registre.testnorge.personsearchservice.controller.dto.FoedselDTO;
import no.nav.registre.testnorge.personsearchservice.controller.dto.PersonDTO;
import no.nav.registre.testnorge.personsearchservice.controller.dto.SivilstandDTO;

public class Person {
    private final Response response;
    private final Statsborgerskap statsborgerskap;
    private final UtfyttingFraNorge utfyttingFraNorge;

    public Person(Response response) {
        this.response = response;
        var statsborgerskap = getCurrent(response.getHentPerson().getStatsborgerskap()).orElse(null);
        this.statsborgerskap = statsborgerskap != null ? new Statsborgerskap(statsborgerskap) : null;
        var utfyttingFraNorge = getCurrent(response.getHentPerson().getUtflyttingFraNorges()).orElse(null);
        this.utfyttingFraNorge = utfyttingFraNorge != null ? new UtfyttingFraNorge(utfyttingFraNorge) : null;
    }

    private static <T extends WithMetadata> Optional<T> getCurrent(List<T> list) {
        return list
                .stream()
                .filter(value -> !value.getMetadata().getHistorisk())
                .findFirst();
    }

    private Optional<Navn> getNavn() {
        return getCurrent(response.getHentPerson().getNavn());
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

    public LocalDate getFoedselsdato() {
        return getCurrent(response.getHentPerson().getFoedsel()).map(Foedsel::getFoedselsdato).orElse(null);
    }

    public String getKjoenn() {
        return getCurrent(response.getHentPerson().getKjoenn()).map(Kjoenn::getKjoenn).orElse(null);
    }

    public String getSivilstand() {
        return getCurrent(response.getHentPerson().getSivilstand()).map(Sivilstand::getType).orElse(null);
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
                .etternavn(getEtternavn())
                .aktorId(getAktorId())
                .ident(getIdent())
                .kjoenn(getKjoenn())
                .tag(getTags().stream().findFirst().orElse(null))
                .foedsel(FoedselDTO.builder().foedselsdato(getFoedselsdato()).build())
                .sivilstand(SivilstandDTO.builder().type(getSivilstand()).build())
                .statsborgerskap(statsborgerskap.toDTO())
                .utfyttingFraNorge(utfyttingFraNorge.toDTO())
                .build();
    }
}
