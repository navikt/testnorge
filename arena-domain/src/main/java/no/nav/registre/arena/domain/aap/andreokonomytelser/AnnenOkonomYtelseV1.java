package no.nav.registre.arena.domain.aap.andreokonomytelser;

import com.fasterxml.jackson.annotation.JsonAlias;
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

    @JsonAlias({ "KODE", "kode" })
    private OkonomKoder kode;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
