package no.nav.resultSet;

import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
public class RsBruker {
	private String navIdent;
	private Set<RsTeamMedIdOgNavn> teams = new HashSet<>();
	private Set<RsTestgruppe> favoritter = new HashSet<>();
}
