package no.nav.testnav.libs.domain.dto.arena.testnorge.vedtak.andreokonomytelser;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AndreOkonomYtelser {

    @JsonAlias({ "ANNEN_OKONOM_YTELSE", "annenOkonomYtelse" })
    private List<AnnenOkonomYtelseAap> annenOkonomYtelse;
}
