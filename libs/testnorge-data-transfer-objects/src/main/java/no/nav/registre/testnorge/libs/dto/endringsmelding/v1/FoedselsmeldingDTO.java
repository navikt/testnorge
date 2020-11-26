package no.nav.registre.testnorge.libs.dto.endringsmelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselsmeldingDTO {
    String identFar;
    String identMor;
    String identtype;
    LocalDate foedselsdato;
    Kjoenn kjoenn;
    AdresseFra adresseFra;
}
