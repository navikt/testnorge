package no.nav.registre.spion.consumer.rs.response.hodejegeren;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class HodejegerenResponse {

    private final String datoInnvandret;
    private final String datoSivilstand;
    private final String datoStatsborger;
    private final String etternavn;
    private final String fnr;
    private final String fornavn;
    private final String innvandretFra;
    private final String kodeInnvandretFra;
    private final String kodeSivilstand;
    private final String kodeStatsborger;
    private final String kortnavn;
    private final String mellomnavn;
    private final String sivilstand;
    private final String statsborger;

}
