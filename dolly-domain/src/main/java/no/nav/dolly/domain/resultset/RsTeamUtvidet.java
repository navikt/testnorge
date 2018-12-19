package no.nav.dolly.domain.resultset;

import java.util.HashSet;
import java.util.Set;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RsTeamUtvidet extends RsTeam {

	private Set<RsTestgruppe> grupper;
	private Set<RsBruker> medlemmer;

	public Set<RsTestgruppe> getGrupper() {
		if (grupper == null) {
			grupper = new HashSet<>();
		}
		return grupper;
	}

	public Set<RsBruker> getMedlemmer() {
		if (medlemmer == null) {
			medlemmer = new HashSet<>();
		}
		return medlemmer;
	}
}
