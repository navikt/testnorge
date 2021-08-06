package no.nav.testnav.libs.domain.dto.arena.testnorge.aap.medisinskopplysning;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
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
