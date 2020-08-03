package no.nav.registre.testnorge.dto.synt.arbeidsforhold.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class SyntArbeidsforholdDTO {
    LocalDate foedselsdato;
    String ident;
}