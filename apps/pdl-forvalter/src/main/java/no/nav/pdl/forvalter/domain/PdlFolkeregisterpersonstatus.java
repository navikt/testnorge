package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
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
