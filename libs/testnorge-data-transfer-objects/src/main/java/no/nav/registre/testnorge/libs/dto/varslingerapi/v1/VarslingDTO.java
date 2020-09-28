package no.nav.registre.testnorge.libs.dto.varslingerapi.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class VarslingDTO {
    String varslingId;
    LocalDate fom;
    LocalDate tom;
}
