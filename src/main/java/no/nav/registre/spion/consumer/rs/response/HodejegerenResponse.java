package no.nav.registre.spion.consumer.rs.response;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class HodejegerenResponse {

    private final String fnr;
    private final String fornavn;
    private final String mellomnavn;
    private final String etternavn;
    private final String kortnavn;
    private final String innvandretFra;
    private final String datoInnvandret;
    private final String kodeInnvandretFra;
    private final String sivilstand;
    private final String datoSivilstand;
    private final String kodeSivilstand;
    private final String statsborger;
    private final String datoStatsborger;
    private final String kodeStatsborger;



}
