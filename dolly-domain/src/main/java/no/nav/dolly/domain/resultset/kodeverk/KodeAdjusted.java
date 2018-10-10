package no.nav.dolly.domain.resultset.kodeverk;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KodeAdjusted {
    private String label;
    private String value;
}
