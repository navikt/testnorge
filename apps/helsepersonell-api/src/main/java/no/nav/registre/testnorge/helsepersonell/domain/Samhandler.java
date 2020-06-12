package no.nav.registre.testnorge.helsepersonell.domain;

import lombok.RequiredArgsConstructor;

import java.util.Optional;

import no.nav.registre.testnorge.dto.samhandlerregisteret.v1.IdentDTO;
import no.nav.registre.testnorge.dto.samhandlerregisteret.v1.SamhandlerDTO;

@RequiredArgsConstructor
public class Samhandler {
    private final SamhandlerDTO dto;

    public String getHprId() {
        if (dto.getIdenter() == null) {
            return null;
        }
        Optional<IdentDTO> hprId = dto.getIdenter()
                .stream()
                .filter(value -> value.getIdentTypeKode() != null && value.getIdentTypeKode().equals("HPR"))
                .findFirst();
        return hprId.isEmpty() ? null : hprId.get().getIdent();
    }
}
