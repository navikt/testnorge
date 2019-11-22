package no.nav.dolly.domain.resultset.entity.testgruppe;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RsOpprettEndreTestgruppe {
    private String navn;
    private String hensikt;
}
