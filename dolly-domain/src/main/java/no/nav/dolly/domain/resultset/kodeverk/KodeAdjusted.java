package no.nav.dolly.domain.resultset.kodeverk;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class KodeAdjusted {
    private String label;
    private String value;
}
