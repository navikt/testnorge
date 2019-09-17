package no.nav.registre.hodejegeren.provider.rs.responses.persondata;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PersondataResponse {

    private String fnr;
    private String kortnavn;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private String kodeStatsborger;
    private String statsborger;
    private String datoStatsborger;
    private String kodeSivilstand;
    private String sivilstand;
    private String datoSivilstand;
    private String kodeInnvandretFra;
    private String innvandretFra;
    private String datoInnvandret;
}
