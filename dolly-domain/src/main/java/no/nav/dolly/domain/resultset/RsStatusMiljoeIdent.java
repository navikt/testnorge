package no.nav.dolly.domain.resultset;

import java.util.Map;
import java.util.Set;

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
public class RsStatusMiljoeIdent {

    private String statusMelding;
    private Map<String, Set<String>> environmentIdents;
}
