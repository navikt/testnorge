package no.nav.registre.arena.domain.vedtak.andreokonomytelser;

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
public class AnnenOkonomYtelseAap {

    @JsonAlias({ "KODE", "kode" })
    private OkonomKoderAap kode;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
