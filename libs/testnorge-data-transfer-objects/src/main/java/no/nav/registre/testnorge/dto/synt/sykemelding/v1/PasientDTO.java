package no.nav.registre.testnorge.dto.synt.sykemelding.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class PasientDTO {
    private String ident;
    private String fornavn;
    private String mellomnavn;
    private String etternavn;
    private LocalDate foedselsdato;
}
