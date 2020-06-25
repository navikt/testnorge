package no.nav.registre.testnorge.helsepersonell.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.dto.helsepersonell.v1.LegeListeDTO;

@RequiredArgsConstructor
public class LegeListe {
    private final List<Lege> leger;

    public LegeListeDTO toDTO() {
        return new LegeListeDTO(leger.stream()
                .map(Lege::toDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList())
        );
    }
}
