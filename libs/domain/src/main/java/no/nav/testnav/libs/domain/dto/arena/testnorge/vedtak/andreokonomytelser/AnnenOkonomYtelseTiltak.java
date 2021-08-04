package no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.andreokonomytelser;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnenOkonomYtelseTiltak {

    @JsonAlias({ "KODE", "kode" })
    private OkonomKoderTiltak kode;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
