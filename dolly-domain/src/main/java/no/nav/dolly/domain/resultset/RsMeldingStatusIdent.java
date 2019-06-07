package no.nav.dolly.domain.resultset;

import java.util.List;
import java.util.Map;

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
public class RsMeldingStatusIdent {

    private String status;
    private Map<String, List<String>> envIdent;
}
