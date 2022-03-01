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
    String excludeTag;
    List<String> excludeTags;
    String kjoenn;
    String randomSeed;
    Boolean kunLevende;
    Boolean kunDoede;
    List<String> identer;
    FoedselSearch foedsel;
    SivilstandSearch sivilstand;
    StatsborgerskapSearch statsborgerskap;
    AlderSearch alder;
    UtflyttingFraNorgeSearch utflyttingFraNorge;
    InnflyttingTilNorgeSearch innflyttingTilNorge;
    IdentitetSearch identitet;
    BarnSearch barn;
    RelasjonSearch relasjoner;
    PersonstatusSearch personstatus;
}