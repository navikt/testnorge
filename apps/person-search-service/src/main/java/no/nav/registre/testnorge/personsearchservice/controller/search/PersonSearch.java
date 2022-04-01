package no.nav.registre.testnorge.personsearchservice.controller.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonSearch {
    Integer page;
    Integer pageSize;
    Integer terminateAfter;
    String tag;
    List<String> excludeTags;
    String kjoenn;
    String randomSeed;
    List<String> identer;
    FoedselSearch foedsel;
    Doedsfall doedsfall;
    SivilstandSearch sivilstand;
    StatsborgerskapSearch statsborgerskap;
    AlderSearch alder;
    UtflyttingFraNorgeSearch utflyttingFraNorge;
    InnflyttingTilNorgeSearch innflyttingTilNorge;
    IdentifikasjonSearch identifikasjon;
    RelasjonSearch relasjoner;
    PersonstatusSearch personstatus;
    AdresserSearch adresser;
}