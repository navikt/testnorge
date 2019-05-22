package no.nav.registre.hodejegeren.mongodb.tpsStatusQuo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Kjerneinfo.Kjerneinformasjon;
import no.nav.registre.hodejegeren.mongodb.tpsStatusQuo.Personrelasjon.Relasjonsinformasjon;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatusQuo {

    private Kjerneinformasjon kjerneinformasjon;
    private Relasjonsinformasjon relasjonsinformasjon;
}
