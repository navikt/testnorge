package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflytting extends PdlDbVersjon {

    private Folkeregistermetadata folkeregistermetadata;
    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
}