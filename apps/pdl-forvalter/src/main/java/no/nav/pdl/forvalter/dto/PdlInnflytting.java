package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlInnflytting {

    private Folkeregistermetadata folkeregistermetadata;
    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
    private String kilde;
}