package no.nav.testnav.libs.dto.arena.testnorge.tilleggsstoenad.tilsyn;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TilsynBarn {

    private List<Barn> barn;
}
