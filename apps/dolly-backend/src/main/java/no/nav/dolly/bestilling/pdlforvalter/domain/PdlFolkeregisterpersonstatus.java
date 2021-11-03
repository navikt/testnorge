package no.nav.dolly.bestilling.pdlforvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PdlFolkeregisterpersonstatus extends PdlOpplysning {

    public enum Folkeregisterpersonstatus {
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

    private Folkeregisterpersonstatus status;
}
