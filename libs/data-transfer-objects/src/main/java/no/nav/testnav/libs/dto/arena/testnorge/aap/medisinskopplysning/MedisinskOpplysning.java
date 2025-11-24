package no.nav.testnav.libs.dto.arena.testnorge.aap.medisinskopplysning;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MedisinskOpplysning {

    @JsonAlias({ "TYPE", "type" })
    private String type;

    @JsonAlias({ "KLASSIFISERING", "klassifisering" })
    private String klassifisering;

    @JsonAlias({ "DIAGNOSE", "diagnose" })
    private String diagnose;

    @JsonAlias({ "KILDE", "kilde" })
    private String kilde;

    @JsonAlias({ "KILDEDATO", "kildeDato" })
    private LocalDate kildeDato;
}
