package no.nav.registre.bisys.domain.search;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class FoedselSearch {
    LocalDate fom;
    LocalDate tom;
}
