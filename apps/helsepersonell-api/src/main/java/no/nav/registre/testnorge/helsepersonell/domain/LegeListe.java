package no.nav.registre.testnorge.helsepersonell.domain;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.LegeListeDTO;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class LegeListe {
    private final List<Lege> leger;

    public LegeListeDTO toDTO() {
        return new LegeListeDTO(leger.stream()
                .map(Lege::toDTO)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList())
        );
    }
}
