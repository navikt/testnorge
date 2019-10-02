package no.nav.registre.arena.domain.aap.andreokonomytelser;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnnenOkonomYtelseV1 {
    private OkonomKoder kode;
    private String verdi;
}
