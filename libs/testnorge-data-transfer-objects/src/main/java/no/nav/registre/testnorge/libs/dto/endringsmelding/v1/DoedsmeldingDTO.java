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
public class DoedsmeldingDTO {
    String ident;
    Handling handling;
    LocalDate doedsdato;
}
