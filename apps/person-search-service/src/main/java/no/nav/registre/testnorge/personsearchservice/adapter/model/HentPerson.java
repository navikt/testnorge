package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPerson {
    List<Navn> navn;
    List<Kjoenn> kjoenn;
    List<Foedsel> foedsel;
    List<Sivilstand> sivilstand;
    List<Statsborgerskap> statsborgerskap;
    List<UtflyttingFraNorge> utflyttingFraNorge;
}
