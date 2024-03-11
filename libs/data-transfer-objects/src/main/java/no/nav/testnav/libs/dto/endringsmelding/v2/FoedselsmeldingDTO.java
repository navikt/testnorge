package no.nav.testnav.libs.dto.endringsmelding.v2;

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
    Identtype identtype;
    LocalDate foedselsdato;
    Kjoenn kjoenn;
    AdresseFra adresseFra;
}
