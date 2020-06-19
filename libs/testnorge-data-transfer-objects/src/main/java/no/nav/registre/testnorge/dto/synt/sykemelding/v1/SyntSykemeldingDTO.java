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
public class SyntSykemeldingDTO {
    PasientDTO pasient;
    LocalDate startDato;
    ArbeidsgiverDTO arbeidsgiver;
}
