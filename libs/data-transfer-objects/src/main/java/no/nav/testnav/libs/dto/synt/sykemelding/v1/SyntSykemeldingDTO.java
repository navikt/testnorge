package no.nav.testnav.libs.dto.synt.sykemelding.v1;

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
    String ident;
    String orgnummer;
    String arbeidsforholdId;
    LocalDate startDato;
}