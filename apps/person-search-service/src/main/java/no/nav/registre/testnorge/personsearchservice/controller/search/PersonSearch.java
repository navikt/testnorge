package no.nav.registre.testnorge.personsearchservice.controller.search;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.personsearchservice.controller.dto.Pageing;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PersonSearch {
    Pageing pageing;
    String tag;
    String excludeTag;
    String kjoenn;
    IdentSearch ident;
    FoedselSearch foedsel;
    SivilstandSearch sivilstand;
    StatsborgerskapSearch statsborgerskap;
    AlderSearch alder;
    UtflyttingFraNorgeSearch utflyttingFraNorge;
    InnflyttingTilNorgeSearch innflyttingTilNorge;
    IdentitetSearch identitet;
    BarnSearch barn;
}