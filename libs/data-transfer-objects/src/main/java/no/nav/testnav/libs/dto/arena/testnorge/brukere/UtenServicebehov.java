package no.nav.testnav.libs.dto.arena.testnorge.brukere;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UtenServicebehov {

    private LocalDate stansDato;
}
