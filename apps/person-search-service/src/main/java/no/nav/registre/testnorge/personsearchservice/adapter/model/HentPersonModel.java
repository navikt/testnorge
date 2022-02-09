package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPersonModel {
    List<NavnModel> navn;
    List<KjoennModel> kjoenn;
    List<FoedselModel> foedsel;
    List<SivilstandModel> sivilstand;
    List<StatsborgerskapModel> statsborgerskap;
    List<UtflyttingFraNorgeModel> utflyttingFraNorge;
    List<InnflyttingTilNorgeModel> innflyttingTilNorge;
    List<PersonstatusModel> folkeregisterpersonstatus;
}
