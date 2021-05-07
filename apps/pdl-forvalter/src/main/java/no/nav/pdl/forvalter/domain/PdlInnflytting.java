package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlInnflytting extends PdlDbVersjon {

    private Folkeregistermetadata folkeregistermetadata;
    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
}