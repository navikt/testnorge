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
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RsBruker {
	private String navIdent;
	private Set<RsTeamMedIdOgNavn> teams;
	private Set<RsTestgruppe> favoritter;

	public Set<RsTeamMedIdOgNavn> getTeams() {
		if (teams == null) {
			teams = new HashSet<>();
		}
		return teams;
	}

	public Set<RsTestgruppe> getFavoritter() {
		if (favoritter == null) {
			favoritter = new HashSet<>();
		}
		return favoritter;
	}
}