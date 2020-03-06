package no.nav.registre.spion.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class HodejegerenResponse {

    String fnr;
    String fornavn;
    String mellomnavn;
    String etternavn;
    String kortnavn;
    String innvandretFra;
    String datoInnvandret;
    String kodeInnvandretFra;
    String sivilstand;
    String datoSivilstand;
    String kodeSivilstand;
    String statsborger;
    String datoStatsborger;
    String kodeStatsborger;


}
