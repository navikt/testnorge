package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PdlFolkeregisterpersonstatus {

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

    private String kilde;
    private Folkeregisterpersonstatus status;
}
