package no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.tilsyn;

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
public class TilsynFamiliemedlemmer {

    private List<Familiemedlem> familiemedlem;
}
