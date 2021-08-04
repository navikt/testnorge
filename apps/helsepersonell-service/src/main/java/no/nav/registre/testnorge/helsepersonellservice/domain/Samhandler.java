package no.nav.registre.testnorge.helsepersonellservice.domain;

import lombok.RequiredArgsConstructor;

import java.util.Optional;
import java.util.Set;

import no.nav.testnav.libs.dto.samhandlerregisteret.v1.IdentDTO;
import no.nav.testnav.libs.dto.samhandlerregisteret.v1.SamhandlerDTO;

@RequiredArgsConstructor
public class Samhandler {
    private static final Set<String> SAMHANDLER_KODE_MED_MULIGHET_TIL_AA_LAGE_SYKEMELDING = Set.of("KI", "LE", "MT", "FT", "TL");

    private final SamhandlerDTO dto;


    private String getIdentByKode(String kode) {
        Optional<IdentDTO> ident = dto.getIdenter()
                .stream()
                .filter(value -> value.getIdentTypeKode() != null && value.getIdentTypeKode().equals(kode))
                .findFirst();
        return ident.isEmpty() ? null : ident.get().getIdent();
    }

    public String getIdent() {
        return getIdentByKode("FNR");
    }

    public String getHprId() {
        return getIdentByKode("HPR");
    }

    public String getSamhandlerType() {
        return dto.getKode();
    }

    public boolean isMulighetForAaLageSykemelding() {
        return SAMHANDLER_KODE_MED_MULIGHET_TIL_AA_LAGE_SYKEMELDING.contains(dto.getKode());
    }

}
