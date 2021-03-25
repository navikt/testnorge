package no.nav.registre.testnorge.helsepersonellservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import no.nav.registre.testnorge.libs.dto.helsepersonell.v1.HelsepersonellListeDTO;

@RequiredArgsConstructor
public class HelsepersonellListe {
    private final List<Helsepersonell> helsepersonell;

    public HelsepersonellListeDTO toDTO() {
        return new HelsepersonellListeDTO(helsepersonell.stream()
                .map(Helsepersonell::toDTO)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList())
        );
    }
}
