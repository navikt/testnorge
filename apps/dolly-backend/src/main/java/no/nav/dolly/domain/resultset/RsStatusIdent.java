package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsStatusIdent {

    private String statusMelding;
    private List<String> identer;
}
