package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PdlFolkeregisterpersonstatus extends PdlDbVersjon {

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
