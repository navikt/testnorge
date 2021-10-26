package no.nav.dolly.domain.resultset.arenaforvalter;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;

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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsMeldingStatusIdent {

    private String status;
    private Map<String, List<String>> envIdent;
}
