package no.nav.testnav.libs.dto.arena.testnorge.vedtak.andreokonomytelser;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AndreOkonomYtelser {

    @JsonAlias({ "ANNEN_OKONOM_YTELSE", "annenOkonomYtelse" })
    private List<AnnenOkonomYtelseAap> annenOkonomYtelse;
}
