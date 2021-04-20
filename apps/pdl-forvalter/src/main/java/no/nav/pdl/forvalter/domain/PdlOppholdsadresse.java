package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlOppholdsadresse extends PdlAdresse {

    private OppholdAnnetSted oppholdAnnetSted;
    private PdlVegadresse vegadresse;
    private PdlUtenlandskAdresse utenlandskAdresse;
    private PdlMatrikkeladresse matrikkeladresse;

}
