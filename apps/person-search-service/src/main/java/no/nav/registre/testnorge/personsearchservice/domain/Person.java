package no.nav.registre.testnorge.personsearchservice.domain;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import no.nav.registre.testnorge.personsearchservice.adapter.model.*;
import no.nav.testnav.libs.dto.personsearchservice.v1.*;

public class Person {
    private final Response response;
    private final Statsborgerskap statsborgerskap;
    private final UtfyttingFraNorge utfyttingFraNorge;
    private final InnflyttingTilNorge innflyttingTilNorge;
    private final ForelderBarnRelasjon forelderBarnRelasjon;

    public Person(Response response) {
        this.response = response;
        var borgerskap = getAllCurrentStatborgerskap(response.getHentPerson().getStatsborgerskap());
        this.statsborgerskap = borgerskap != null ? new Statsborgerskap(borgerskap) : null;
        var utfytting = getCurrent(response.getHentPerson().getUtflyttingFraNorge()).orElse(null);
        this.utfyttingFraNorge = utfytting != null ? new UtfyttingFraNorge(utfytting) : null;
        var innflytting = getCurrent(response.getHentPerson().getInnflyttingTilNorge()).orElse(null);
        this.innflyttingTilNorge = innflytting != null ? new InnflyttingTilNorge(innflytting) : null;
        var relasjoner = response.getHentPerson().getForelderBarnRelasjon();
        this.forelderBarnRelasjon = relasjoner != null ? new ForelderBarnRelasjon(relasjoner) : null;
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

    private static List<StatsborgerskapModel> getAllCurrentStatborgerskap(List<StatsborgerskapModel> list) {
        if(list == null) {
            return Collections.emptyList();
        }
        return list.stream().filter(value -> !value.getMetadata().getHistorisk()).toList();
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

    private LocalDate getDoedsdato(){
        return getCurrent(response.getHentPerson().getDoedsfall()).map(DoedsfallModel::getDoedsdato).orElse(null);
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

    private List<FolkeregisterpersonstatusDTO> getPersonstatus() {
        return response
                .getHentPerson()
                .getFolkeregisterpersonstatus()
                .stream()
                .filter(personstatus ->  !personstatus.getMetadata().getHistorisk())
                .map(personstatus -> FolkeregisterpersonstatusDTO.builder()
                        .status(personstatus.getStatus())
                        .gyldighetstidspunkt(personstatus.getFolkeregistermetadata().getGyldighetstidspunkt())
                        .build()
                )
                .toList();
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
                .doedsfall(DoedsfallDTO.builder().doedsdato(getDoedsdato()).build())
                .sivilstand(SivilstandDTO.builder().type(getSivilstand()).build())
                .statsborgerskap(toDTO(statsborgerskap))
                .utfyttingFraNorge(toDTO(utfyttingFraNorge))
                .innfyttingTilNorge(toDTO(innflyttingTilNorge))
                .forelderBarnRelasjoner(toDTO(forelderBarnRelasjon))
                .folkeregisterpersonstatus(getPersonstatus())
                .build();
    }
}
