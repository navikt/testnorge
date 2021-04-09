package no.nav.pdl.forvalter.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PdlUtflytting {

    private Folkeregistermetadata folkeregistermetadata;
    private String kilde;
    private String tilflyttingsland;
    private String tilflyttingsstedIUtlandet;
}