package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import no.nav.testnav.libs.dto.pdlforvalter.v1.ForelderBarnRelasjonDTO;
import no.nav.testnav.libs.dto.pdlforvalter.v1.RelatertBiPersonDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PersonUtenIdentifikatorRequest extends RelatertBiPersonDTO {

    private ForelderBarnRelasjonDTO.Rolle minRolle;
}
