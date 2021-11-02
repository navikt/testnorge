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
public class PdlInnflytting extends PdlOpplysning {

    private Folkeregistermetadata folkeregistermetadata;
    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
}