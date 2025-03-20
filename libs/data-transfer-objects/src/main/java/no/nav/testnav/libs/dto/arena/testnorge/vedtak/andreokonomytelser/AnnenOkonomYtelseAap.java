package no.nav.testnav.libs.dto.arena.testnorge.vedtak.andreokonomytelser;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnenOkonomYtelseAap {

    @JsonAlias({ "KODE", "kode" })
    private OkonomKoderAap kode;

    @JsonAlias({ "VERDI", "verdi" })
    private String verdi;
}
