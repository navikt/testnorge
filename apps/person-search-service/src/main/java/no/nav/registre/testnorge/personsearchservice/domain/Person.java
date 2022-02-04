package no.nav.registre.testnorge.personsearchservice.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.personsearchservice.adapter.model.FoedselModel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.KjoennModel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.NavnModel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.Response;
import no.nav.registre.testnorge.personsearchservice.adapter.model.SivilstandModel;
import no.nav.registre.testnorge.personsearchservice.adapter.model.WithMetadata;
import no.nav.registre.testnorge.personsearchservice.controller.dto.FoedselDTO;
import no.nav.registre.testnorge.personsearchservice.controller.dto.PersonDTO;
import no.nav.registre.testnorge.personsearchservice.controller.dto.SivilstandDTO;

public class Person {
    private final Response response;
    private final Statsborgerskap statsborgerskap;
    private final UtfyttingFraNorge utfyttingFraNorge;
    private final InnflyttingTilNorge innflyttingTilNorge;

    public Person(Response response) {
        this.response = response;
        var borgerskap = getCurrent(response.getHentPerson().getStatsborgerskap()).orElse(null);
        this.statsborgerskap = borgerskap != null ? new Statsborgerskap(borgerskap) : null;
        var utfytting = getCurrent(response.getHentPerson().getUtflyttingFraNorge()).orElse(null);
        this.utfyttingFraNorge = utfytting != null ? new UtfyttingFraNorge(utfytting) : null;
        var innflytting = getCurrent(response.getHentPerson().getInnflyttingTilNorge()).orElse(null);
        this.innflyttingTilNorge = innflytting != null ? new InnflyttingTilNorge(innflytting) : null;
    }

    private static <T extends WithMetadata> Optional<T> getCurrent(List<T> list) {
        if(list == null) {
            return Optional.empty();
        }
        return list
                .stream()
                .filter(value -> !value.getMetadata().getHistorisk())
                .findFirst();
    }

    private Optional<NavnModel> getNavn() {
        return getCurrent(response.getHentPerson().getNavn());
    }

    private String getFornavn() {
        return getNavn().map(NavnModel::getFornavn).orElse(null);
    }

    private String getMellomnavn() {
        return getNavn().map(NavnModel::getMellomnavn).orElse(null);
    }

    private String getEtternavn() {
        return getNavn().map(NavnModel::getEtternavn).orElse(null);
    }

    private LocalDate getFoedselsdato() {
        return getCurrent(response.getHentPerson().getFoedsel()).map(FoedselModel::getFoedselsdato).orElse(null);
    }

    private String getKjoenn() {
        return getCurrent(response.getHentPerson().getKjoenn()).map(KjoennModel::getKjoenn).orElse(null);
    }

    private String getSivilstand() {
        return getCurrent(response.getHentPerson().getSivilstand()).map(SivilstandModel::getType).orElse(null);
    }

    private String getIdent() {
        return response
                .getHentIdenter()
                .getIdenter()
                .stream()
                .filter(identer -> identer.getGruppe().equals("FOLKEREGISTERIDENT"))
                .findFirst()
                .orElseThrow()
                .getIdent();
    }

    private String getAktorId() {
        return response
                .getHentIdenter()
                .getIdenter()
                .stream()
                .filter(identer -> identer.getGruppe().equals("AKTORID"))
                .findFirst()
                .orElseThrow()
                .getIdent();
    }

    private List<String> getTags() {
        return response.getTags();
    }

    private <T> T toDTO(WithDTO<T> withDTO){
        return Optional.ofNullable(withDTO).map(WithDTO::toDTO).orElse(null);
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
                .tags(getTags())
                .foedsel(FoedselDTO.builder().foedselsdato(getFoedselsdato()).build())
                .sivilstand(SivilstandDTO.builder().type(getSivilstand()).build())
                .statsborgerskap(toDTO(statsborgerskap))
                .utfyttingFraNorge(toDTO(utfyttingFraNorge))
                .innfyttingTilNorge(toDTO(innflyttingTilNorge))
                .build();
    }
}
