package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaArbeidssokerBruker {

    private HttpStatus status;
    private String feilmelding;
    private String miljoe;
    private List<ArenaBruker> arbeidsokerList;
    private Long antallSider;

    public List<ArenaBruker> getArbeidsokerList() {

        if (isNull(arbeidsokerList)) {
            arbeidsokerList = new ArrayList<>();
        }
        return arbeidsokerList;
    }
}
