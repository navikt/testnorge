package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlOppholdsadresse extends PdlAdresse {

    private OppholdAnnetSted oppholdAnnetSted;
    private PdlVegadresse vegadresse;
    private PdlUtenlandskAdresse utenlandskAdresse;
    private PdlMatrikkeladresse matrikkeladresse;

}
