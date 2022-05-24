package no.nav.dolly.domain.resultset.tpsf;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.isNull;

@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckStatusResponse {

    private List<IdentStatus> statuser;

    public List<IdentStatus> getStatuser() {
        if (isNull(statuser)) {
            statuser = new ArrayList<>();
        }
        return statuser;
    }
}
