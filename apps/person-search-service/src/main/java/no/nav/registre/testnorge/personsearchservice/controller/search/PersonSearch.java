package no.nav.registre.testnorge.personsearchservice.controller.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.testnav.libs.dto.personsearchservice.v1.Pageing;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonSearch {
    Pageing pageing;
    String tag;
    String excludeTag;
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
    PersonstatusSearch personstatus;
}