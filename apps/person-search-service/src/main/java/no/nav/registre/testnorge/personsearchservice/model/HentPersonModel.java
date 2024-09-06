package no.nav.registre.testnorge.personsearchservice.model;

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
    List<FoedselsdatoModel> foedselsdato;
    List<DoedsfallModel> doedsfall;
    List<SivilstandModel> sivilstand;
    List<StatsborgerskapModel> statsborgerskap;
    List<UtflyttingFraNorgeModel> utflyttingFraNorge;
    List<InnflyttingTilNorgeModel> innflyttingTilNorge;
    List<PersonstatusModel> folkeregisterpersonstatus;
    List<ForelderBarnRelasjonModel> forelderBarnRelasjon;
}
