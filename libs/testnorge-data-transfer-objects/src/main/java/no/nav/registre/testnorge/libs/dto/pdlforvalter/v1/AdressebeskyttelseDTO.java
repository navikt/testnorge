package no.nav.registre.testnorge.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import no.nav.registre.testnorge.libs.dto.pdlforvalter.v1.AdresseDTO.Master;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class AdressebeskyttelseDTO extends DbVersjonDTO {

    public enum AdresseBeskyttelse {STRENGT_FORTROLIG_UTLAND, STRENGT_FORTROLIG, FORTROLIG, UGRADERT}

    private AdresseBeskyttelse gradering;
    private Master master;
}
