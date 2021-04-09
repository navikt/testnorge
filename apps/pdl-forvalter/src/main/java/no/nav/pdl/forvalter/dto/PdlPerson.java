package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlPerson {

    private List<PdlBostedadresse> bostedsadresse;
    private List<PdlOppholdsadresse> oppholdsadresse;
    private List<PdlDeltBosted> deltBosted;
    private List<PdlFamilierelasjon> familierelasjoner;
//    private List<PdlFo>

}
