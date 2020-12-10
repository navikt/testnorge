package no.nav.registre.sdforvalter.consumer.rs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class KodeDTO {
    String navn;
    String term;
    LocalDate gyldigFra;
}
