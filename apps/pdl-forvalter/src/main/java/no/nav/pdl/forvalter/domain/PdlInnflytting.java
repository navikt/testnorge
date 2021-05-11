package no.nav.pdl.forvalter.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PdlInnflytting extends PdlDbVersjon {

    private String fraflyttingsland;
    private String fraflyttingsstedIUtlandet;
}