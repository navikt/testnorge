package no.nav.registre.testnorge.synt.arbeidsforhold.consumer.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AnsettelsePeriodeDTO {
    LocalDate fom;
    LocalDate tom;
}
