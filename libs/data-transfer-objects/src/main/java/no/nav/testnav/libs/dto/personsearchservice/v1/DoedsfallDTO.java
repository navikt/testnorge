package no.nav.testnav.libs.dto.personsearchservice.v1;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class DoedsfallDTO {
    LocalDate doedsdato;
}
