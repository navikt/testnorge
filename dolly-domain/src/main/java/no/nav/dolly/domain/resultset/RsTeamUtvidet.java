package no.nav.dolly.domain.resultset;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTeamUtvidet extends RsTeam {

    private List<RsTestgruppe> grupper;
    private List<RsBruker> medlemmer;

    public List<RsTestgruppe> getGrupper() {
        if (grupper == null) {
            grupper = new ArrayList<>();
        }
        return grupper;
    }

    public List<RsBruker> getMedlemmer() {
        if (medlemmer == null) {
            medlemmer = new ArrayList<>();
        }
        return medlemmer;
    }
}
