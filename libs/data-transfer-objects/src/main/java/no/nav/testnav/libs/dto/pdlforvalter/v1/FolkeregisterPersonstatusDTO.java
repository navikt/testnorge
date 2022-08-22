package no.nav.testnav.libs.dto.pdlforvalter.v1;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class FolkeregisterPersonstatusDTO extends DbVersjonDTO {

    public enum FolkeregisterPersonstatus {
        BOSATT,
        UTFLYTTET,
        FORSVUNNET,
        DOED,
        OPPHOERT,
        FOEDSELSREGISTRERT,
        IKKE_BOSATT,
        MIDLERTIDIG,
        INAKTIV
    }

    private FolkeregisterPersonstatus status;
    private LocalDateTime gyldigFraOgMed;
    private LocalDateTime gyldigTilOgMed;
}
