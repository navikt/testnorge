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
public class DoedsmeldingDTO {

    String ident;
    LocalDate doedsdato;
}
