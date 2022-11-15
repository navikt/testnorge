package no.nav.dolly.domain.resultset.arenaforvalter;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ArenaNyeBrukere {

    private List<ArenaNyBruker> nyeBrukere;

    public List<ArenaNyBruker> getNyeBrukere() {
        if (isNull(nyeBrukere)) {
            nyeBrukere = new ArrayList<>();
        }
        return nyeBrukere;
    }
}
